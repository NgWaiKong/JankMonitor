package ngwaikong.com.jankmonitor.toolbox.sampler

import ngwaikong.com.jankmonitor.toolbox.extra.Config
import ngwaikong.com.jankmonitor.toolbox.extra.clearOverSize
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Created by weijiangwu on 2018/3/26.
 */
class CpuSampler(private val mBusyTime: Int, private val mMaxEntryCount: Int, override var mSampleInterval: Long, override var mStartDelay: Long) : BaseSampler() {


    private val mCpuInfoEntries = LinkedHashMap<Long, String>()

    private var mPid = 0
    private var mUserLast: Long = 0
    private var mSystemLast: Long = 0
    private var mIdleLast: Long = 0
    private var mIoWaitLast: Long = 0
    private var mTotalLast: Long = 0
    private var mAppCpuTimeLast: Long = 0

    override fun start() {
        reset()
        super.start()
    }

    fun getCpuRateInfo(): String {
        return join(mCpuInfoEntries)
    }

    //如果在mBusyTime没有读取下一条cpu信息，表示cpu忙，来不及处理本pid的任务，导致应用出现超时。
    fun isCpuBusy(start: Long, end: Long): Boolean {
        if (end - start > mSampleInterval) {
            isOverInterval(start, mSampleInterval, mCpuInfoEntries, mBusyTime)
        }
        return false
    }

    override fun onDoSample() {

        try {
            // 文件/proc/stat的第一行表示CPU的总体情况
            // user             处于用户态的运行时间，不包含 nice值为负的进程
            // nice             nice值为负的进程所占用的CPU时间
            // system           处于核心态的运行时间
            // idle             除IO等待时间以外的其它等待时间
            // iowait           从系统启动开始累计到当前时刻，IO等待时间
            // irq              硬中断时间
            // irq              软中断时间
            // stealstolen      一个其他的操作系统运行在虚拟环境下所花费的时间
            // guest            这是在Linux内核控制下为客户操作系统运行虚拟CPU所花费的时间
            val cpuRate = readContent("/proc/stat")

            if (mPid == 0) {
                mPid = android.os.Process.myPid()
            }

            // 文件/proc/pid/stat包含了某一进程所有的活动的信息,split(" ")后，与cpu使用率有关的值是(单位为jiffies)：
            // index = 13: utime    该任务在用户态运行的时间
            // index = 14: stime    该任务在核心态运行的时间
            // index = 15: cutime   所有已死线程在用户态运行的时间
            // index = 16: cstime   所有已死在核心态运行的时间
            val pidCpuRate = readContent("/proc/$mPid/stat")

            parse(cpuRate, pidCpuRate)
        } catch (throwable: Throwable) {
            //TODO(for log)
        }

    }

    private fun readContent(filePath: String): String {
        return BufferedReader(InputStreamReader(
                FileInputStream(filePath)), Config.BUFFER_SIZE).use {
            val content = it.readLine()
            if (content.isNullOrEmpty()) "" else content
        }
    }

    fun isOverInterval(start: Long, interval: Long, map: LinkedHashMap<Long, String>, threshold: Int): Boolean {
        val s = start - interval
        val e = start + interval
        var last: Long = 0
        synchronized(map) {
            for ((time) in map) {
                if (time in (s + 1)..(e - 1)) {
                    if (last != 0L && time - last > threshold) {
                        return true
                    }
                    last = time
                }
            }
        }
        return false
    }

    fun join(map: LinkedHashMap<Long, String>): String {
        val sb = StringBuilder()
        synchronized(map) {
            for ((time, value) in map) {
                sb.append(Config.TIME_FORMATTER.format(time))
                        .append(' ')
                        .append(value)
                        .append(Config.SEPARATOR)
            }
        }
        return sb.toString()
    }

    private fun parse(cpuRate: String, pidCpuRate: String) {
        //parse cpu
        val cpuInfoArray = cpuRate.split(" ".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (cpuInfoArray.size < 9) {
            return
        }
        val user = java.lang.Long.parseLong(cpuInfoArray[2])
        val nice = java.lang.Long.parseLong(cpuInfoArray[3])
        val system = java.lang.Long.parseLong(cpuInfoArray[4])
        val idle = java.lang.Long.parseLong(cpuInfoArray[5])
        val ioWait = java.lang.Long.parseLong(cpuInfoArray[6])
        val total = (user + nice + system + idle + ioWait
                + java.lang.Long.parseLong(cpuInfoArray[7])
                + java.lang.Long.parseLong(cpuInfoArray[8]))


        //parse pid
        val pidCpuInfoList = pidCpuRate.split(" ".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (pidCpuInfoList.size < 17) {
            return
        }

        val appCpuTime = (java.lang.Long.parseLong(pidCpuInfoList[13])
                + java.lang.Long.parseLong(pidCpuInfoList[14])
                + java.lang.Long.parseLong(pidCpuInfoList[15])
                + java.lang.Long.parseLong(pidCpuInfoList[16]))

        val totalTime = total - mTotalLast
        if (mTotalLast != 0L && totalTime > 0) {
            val stringBuilder = StringBuilder()
            val idleTime = idle - mIdleLast

            stringBuilder
                    .append("cpu:")
                    .append((totalTime - idleTime) * 100L / totalTime)
                    .append("% ")
                    .append("app:")
                    .append((appCpuTime - mAppCpuTimeLast) * 100L / totalTime)
                    .append("% ")
                    .append("[")
                    .append("user:").append((user - mUserLast) * 100L / totalTime)
                    .append("% ")
                    .append("system:").append((system - mSystemLast) * 100L / totalTime)
                    .append("% ")
                    .append("ioWait:").append((ioWait - mIoWaitLast) * 100L / totalTime)
                    .append("% ]")

            synchronized(mCpuInfoEntries) {
                clearOverSize(mCpuInfoEntries, mMaxEntryCount)
                mCpuInfoEntries.put(System.currentTimeMillis(), stringBuilder.toString())
            }
        }

        mUserLast = user
        mSystemLast = system
        mIdleLast = idle
        mIoWaitLast = ioWait
        mTotalLast = total
        mAppCpuTimeLast = appCpuTime
    }

    private fun reset() {
        mUserLast = 0
        mSystemLast = 0
        mIdleLast = 0
        mIoWaitLast = 0
        mTotalLast = 0
        mAppCpuTimeLast = 0
    }

}