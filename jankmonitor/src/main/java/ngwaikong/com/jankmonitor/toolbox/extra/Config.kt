package ngwaikong.com.jankmonitor.toolbox.extra

import java.text.SimpleDateFormat

/**
 * Created by weijiangwu on 2018/1/27.
 */
class Config {
    companion object {
        const val JM_TAG: String = "JankMonitor@"
        const val FLOATING_WINDOW_REFRESH_TIME: Long = 0L
        const val V_SYNC_RATE = 16
        const val DEVICE_REFRESH_RATE_IN_NS = 16 * 1000000L
        const val DEVICE_REFRESH_RATE_IN_MS = 16L
        const val DEVICE_REFRESH_RATE_IN_MS_FLOAT = 16.67f
        const val DEFAULT_SAMPLE_INTERVAL = 300L
        const val SEPARATOR = "\r\n"
        val TIME_FORMATTER: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        const val BUFFER_SIZE = 1024
        const val PRINTER_DISPATCHING = "Dispatching to"
        const val PRINTER_FINISHED = "Finished to"
        const val SWALLOW_EXCEPTION = true
    }
}