package ngwaikong.com.jankmonitor.toolbox.sampler

import android.os.Build
import android.os.Trace
import android.util.Log
import ngwaikong.com.jankmonitor.toolbox.extra.HookJava
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by weijiangwu on 2018/4/30.
 */
class ViewSampler(override var mSampleInterval: Long, override var mStartDelay: Long) : BaseSampler(), HookJava.onTraceHook {
    companion object {
        private const val TRACE_TAG_VIEW = 1L shl 3
        private val mTraceInfoStack: Stack<TraceInfo> = Stack()
        private val mViewInfoList: ArrayList<ViewInfo> = ArrayList()

        @JvmStatic
        fun traceBegin(traceTag: Long, methodName: String) {
            if (traceTag != TRACE_TAG_VIEW) return

            val traceInfo = TraceInfo.acquire()
            traceInfo.methodName = methodName
            traceInfo.startTimeMs = System.currentTimeMillis()
            mTraceInfoStack.push(traceInfo)
        }

        //do not delete or rename, this method is for the hook
        @JvmStatic
        fun traceEnd(traceTag: Long) {
            if (traceTag == TRACE_TAG_VIEW) {
                if (!mTraceInfoStack.isEmpty()) {
                    val endTime = System.currentTimeMillis()
                    val tracePoint = mTraceInfoStack.pop()
                    var type = ViewInfo.TYPE_UNKNOWN
                    when (tracePoint.methodName) {
                        "input" -> type = ViewInfo.TYPE_INPUT
                        "animation" -> type = ViewInfo.TYPE_ANIMATION
                        "inflate" -> type = ViewInfo.TYPE_INFLATE
                        "measure" -> type = ViewInfo.TYPE_MEASURE
                        "layout" -> type = ViewInfo.TYPE_LAYOUT
                        "draw" -> type = ViewInfo.TYPE_DRAW
                        "commit" -> type = ViewInfo.TYPE_COMMIT
                    }

                    if (type != ViewInfo.TYPE_UNKNOWN) {
                        val info = ViewInfo(type, tracePoint.startTimeMs, endTime)
                        synchronized(mViewInfoList) {
                            mViewInfoList.add(info)
                        }
                    }

                    tracePoint.recycle()
                }
            }
        }
    }

    private var mIsSupported: Boolean = false
    var onDumpFunc: ((viewInfoList: ArrayList<ViewInfo>) -> Unit?)? = null


    override fun onTraceBegin(traceTag: Long, methodName: String) {
        traceBegin(traceTag, methodName)
    }

    override fun onTraceEnd(traceTag: Long) {
        traceEnd(traceTag)
    }

    override fun start() {
        init()
        super.start()
    }

    override fun onDoSample() {
        if (!mIsSupported) return
        onDumpFunc?.invoke(getViewInfoList())
    }

    override fun stop() {
        super.stop()
        clearViewInfoList()
        TraceInfo.clear()
    }


    private fun init() {
        if (!isSupported()) return
        mIsSupported = true
        initHook()
    }


    private fun initHook() {
        try {
            val traceBegin = Trace::class.java.getDeclaredMethod("traceBegin", Long::class.javaPrimitiveType, String::class.java)
            val traceEnd = Trace::class.java.getDeclaredMethod("traceEnd", Long::class.javaPrimitiveType)
            HookJava.setOnTraceHook(this)
            HookJava.hook(traceBegin, HookJava::class.java.getDeclaredMethod("traceBegin", Long::class.javaPrimitiveType, String::class.java))
            HookJava.hook(traceEnd, HookJava::class.java.getDeclaredMethod("traceEnd", Long::class.javaPrimitiveType))
        } catch (throwable: Throwable) {
            Log.e("HookJava", throwable.toString())
        }
    }

    private fun getViewInfoList(): ArrayList<ViewInfo> {
        synchronized(mViewInfoList) {
            return ArrayList(mViewInfoList)
        }
    }

    private fun clearViewInfoList() {
        synchronized(mViewInfoList) {
            mViewInfoList.clear()
        }
    }


    private fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT in 20..25
    }

    class TraceInfo {

        //Business field
        var startTimeMs: Long = 0
        var methodName: String = ""

        //assistance
        private var mNext: TraceInfo? = null

        companion object {
            //assistance
            private const val MAX_POOL_SIZE = 100
            private val sPoolSync = Any()
            private var mPoolSize: Int = 0
            private var mPool: TraceInfo? = null

            fun acquire(): TraceInfo {
                synchronized(sPoolSync) {
                    return if (mPool != null) {
                        val ti = mPool
                        mPool = ti!!.mNext
                        ti.mNext = null
                        mPoolSize--
                        ti
                    } else {
                        TraceInfo()
                    }
                }
            }

            fun clear() {
                synchronized(sPoolSync) {
                    mPool = null
                    mPoolSize = 0
                }
            }
        }

        fun recycle() {
            startTimeMs = 0
            methodName = ""
            synchronized(sPoolSync) {
                if (mPoolSize < MAX_POOL_SIZE) {
                    this.mNext = mPool
                    mPool = this
                    mPoolSize++
                }
            }
        }
    }

    data class ViewInfo(private val type: Int, private val startTimeMs: Long, private val endTimeMs: Long) {
        private val costTimeMs: Long
            get() = endTimeMs - startTimeMs

        override fun toString(): String {
            val type: String = when (this.type) {
                TYPE_INPUT -> "input"
                TYPE_ANIMATION -> "animation"
                TYPE_INFLATE -> "inflate"
                TYPE_MEASURE -> "measure"
                TYPE_LAYOUT -> "layout"
                TYPE_DRAW -> "draw"
                TYPE_COMMIT -> "commit"
                else -> "unknown"
            }
            return type + ":" + costTimeMs + "ms"
        }

        companion object {
            internal const val TYPE_UNKNOWN = -1
            internal const val TYPE_INPUT = 0
            internal const val TYPE_ANIMATION = 1
            internal const val TYPE_INFLATE = 2
            internal const val TYPE_MEASURE = 3
            internal const val TYPE_LAYOUT = 4
            internal const val TYPE_DRAW = 5
            internal const val TYPE_COMMIT = 6
        }
    }

}

