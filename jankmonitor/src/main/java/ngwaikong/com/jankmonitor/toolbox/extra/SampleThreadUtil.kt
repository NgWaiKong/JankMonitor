package ngwaikong.com.jankmonitor.toolbox.extra

import android.os.Handler
import android.os.HandlerThread

/**
 * Created by weijiangwu on 2018/1/27.
 */
object SampleThreadUtil {
    private const val THREAD_NAME = "sample-thread"

    private val sWorkerThread: HandlerThread = HandlerThread(Config.JM_TAG + THREAD_NAME)
    val sWorkerHandler: Handler

    init {
        sWorkerThread.start()
        sWorkerHandler = Handler(sWorkerThread.looper)
    }

    fun newHandlerThread(name: String): HandlerThread {
        return HandlerThread(name)
    }

}