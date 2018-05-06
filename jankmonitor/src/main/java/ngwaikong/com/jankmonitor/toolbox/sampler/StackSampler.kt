package ngwaikong.com.jankmonitor.toolbox.sampler

import ngwaikong.com.jankmonitor.toolbox.extra.Config
import ngwaikong.com.jankmonitor.toolbox.extra.clearOverSize

/**
 * Created by weijiangwu on 2018/3/26.
 */
class StackSampler(private val mThread: Thread, private val mMaxEntryCount: Int, override var mSampleInterval: Long, override var mStartDelay: Long) : BaseSampler() {

    private val sStackMap: LinkedHashMap<Long, String> = LinkedHashMap()

    override fun onDoSample() {
        // get stack to string
        val traceStr = generateStackTrace(mThread)

        synchronized(sStackMap) {
            //over max count will clear map
            clearOverSize(sStackMap, mMaxEntryCount)

            sStackMap.put(System.currentTimeMillis(), traceStr)
        }
    }

    fun getThreadStackEntries(startTimeMs: Long, endTimeMs: Long): List<String> {
        return split(sStackMap, startTimeMs, endTimeMs)
    }

    fun split(map: LinkedHashMap<Long, String>, startTimeMs: Long, endTimeMs: Long): List<String> {
        val result: ArrayList<String> = ArrayList()
        synchronized(map) {
            map.keys.forEach {
                //startTimeMs < it && endTimeMs> it
                if (it in (startTimeMs + 1)..(endTimeMs - 1)) {
                    result.add(Config.TIME_FORMATTER.format(it)
                            + Config.SEPARATOR
                            + map[it])
                }
            }
        }
        return result
    }

    fun generateStackTrace(thread: Thread): String {
        val stringBuilder = StringBuilder()
        thread.stackTrace.forEach {
            stringBuilder
                    .append(it.toString())
                    .append(Config.SEPARATOR)
        }
        return stringBuilder.toString()
    }


}