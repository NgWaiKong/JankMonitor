package ngwaikong.com.jankmonitor.core

/**
 * Created by weijiangwu on 2018/1/27.
 */
object ChoreographerManager {
    private val frameCallbackList: ArrayList<BaseFrameCallback> = ArrayList()

    fun start(frameCallback: BaseFrameCallback) {
        if (!frameCallbackList.contains(frameCallback)) {
            frameCallbackList.add(frameCallback)
        }
        frameCallback.start()
    }

    fun stop() {
        frameCallbackList.forEach { it.stop() }
    }

}