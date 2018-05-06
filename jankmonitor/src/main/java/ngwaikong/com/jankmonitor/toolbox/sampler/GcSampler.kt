package ngwaikong.com.jankmonitor.toolbox.sampler

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import ngwaikong.com.jankmonitor.toolbox.extra.Config
import ngwaikong.com.jankmonitor.toolbox.extra.closeDataObject
import ngwaikong.com.jankmonitor.toolbox.extra.SampleThreadUtil
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by weijiangwu on 2018/4/30.
 */
class GcSampler(override var mSampleInterval: Long, override var mStartDelay: Long, private val mPid: Int) : BaseSampler() {
    companion object {
        private const val GC_THREAD_NAME = "gc-sampler"
        private const val CLEAR_LOGCAT_CMD = "logcat -c"
        private const val UN_FORMAT_LOGCAT_CMD = "logcat -v time %s:D *:S | grep '%d)'"
    }

    private val mGcInfoList: ArrayList<GcInfo> = ArrayList()
    private val mGcSamplerThread: HandlerThread = SampleThreadUtil.newHandlerThread(Config.JM_TAG + GC_THREAD_NAME)
    private var mGcHandler: Handler? = null
    private var mOnRecordFunc: ((gcInfo: GcInfo, gcInfoList: ArrayList<GcInfo>) -> Unit)? = null
    override val mRunnable = Runnable { onDoSample() }

    override fun stop() {
        super.stop()
        mGcHandler?.removeCallbacksAndMessages(null)
        mGcSamplerThread.quitSafely()
    }

    fun setOnRecordListener(onRecordFunc: ((gcInfo: GcInfo, gcInfoList: ArrayList<GcInfo>) -> Unit)) {
        this.mOnRecordFunc = onRecordFunc
    }

    override fun onDoSample() {

        mGcHandler = Handler(mGcSamplerThread.looper)
        mGcSamplerThread.start()
        mGcHandler?.post {
            val isArt = isArt()
            val logcatCmd = String.format(Locale.getDefault(), UN_FORMAT_LOGCAT_CMD, if (isArt) "art" else "dalvikvm-heap:D dalvikvm", mPid)
            val runtime = Runtime.getRuntime()
            var process: Process
            var inputStream: InputStream
            var bufferedReader: BufferedReader? = null
            while (true) {
                try {
                    runtime.exec(CLEAR_LOGCAT_CMD).waitFor()
                    process = runtime.exec(logcatCmd)
                    inputStream = process.inputStream
                    bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val gcLog: String? = bufferedReader.readLine()
                    while (gcLog != null) {
                        if (!gcLog.contains("GC")) {
                            if (isArt) {
                                //need collect 'art : Suspending all threads took: xxx ms'
                                if (!gcLog.contains("Suspending")) {
                                    continue
                                }
                            } else if (!gcLog.contains("dalvikvm-heap")) {
                                //need collect 'Grow heap (frag case) to xxx MB for xxx-byte allocation'
                                continue
                            }
                        }
                        recordGcInfo(System.currentTimeMillis(), gcLog)
                    }
                } catch (throwable: Throwable) {

                } finally {
                    bufferedReader.closeDataObject(Config.SWALLOW_EXCEPTION)
                }
            }
        }
    }

    private fun recordGcInfo(currentTimeMillis: Long, gcLog: String) {
        synchronized(mGcInfoList) {
            val size = mGcInfoList.size
            if (size >= 1) {
                //fix the bug: some system will print same gc log twice
                if (mGcInfoList[size - 1].log == gcLog) {
                    return
                }
            }
            mGcInfoList.add(GcInfo(currentTimeMillis, gcLog))
        }
        mOnRecordFunc?.invoke(mGcInfoList.last(), mGcInfoList)
    }

    private fun isArt(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            true
        } else {
            val vmVersion = System.getProperty("java.vm.version")
            vmVersion != null && vmVersion.startsWith("2")
        }
    }

    data class GcInfo(val time: Long, val log: String)
}