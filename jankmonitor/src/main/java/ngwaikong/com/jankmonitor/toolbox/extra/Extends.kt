package ngwaikong.com.jankmonitor.toolbox.extra

import android.os.Looper
import android.util.Log
import android.util.Printer
import ngwaikong.com.jankmonitor.toolbox.sampler.BaseSampler
import java.io.Closeable
import java.util.concurrent.TimeUnit

/**
 * Created by weijiangwu on 2018/1/23.
 */
fun Long.compareWithValue(dst: Long, threshold: Long): Boolean {
    return (this - threshold) > dst
}

fun Long.nsToMs(): Long {
    return TimeUnit.MILLISECONDS.convert(this, TimeUnit.NANOSECONDS)
}

fun BaseSampler.clearOverSize(map: LinkedHashMap<Long, String>, maxCount: Int) {
    if ((map.size == maxCount || map.size > maxCount) && maxCount > 0) {
        map.remove(map.keys.iterator().next())
    }
}

fun <T> Class<T>.getDeclaredField(obj: Any?, name: String, swallowException: Boolean): Any? {
    val out: Any?
    try {
        val f = this.getDeclaredField(name)
        val oldAccessibleValue = f.isAccessible
        f.isAccessible = true
        out = f.get(obj)
        f.isAccessible = oldAccessibleValue
        return out
    } catch (throwable: Throwable) {
        if (swallowException) {
            //todo fix the log
            Log.i("getDeclaredField", throwable.toString())
            return null
        } else {
            throw throwable
        }
    }
}


fun Closeable?.closeDataObject(swallowException: Boolean) {
    try {
        this?.close()
    } catch (throwable: Throwable) {
        if (swallowException) {
            //todo fix the log
            Log.i("closeDataObject", throwable.toString())
        } else {
            throw throwable
        }
    }
}

fun Looper?.getLogging(): Printer? {
    if (this == null) return null
    val looperClazz = Looper::class.java
    val reflectLogging = looperClazz.getDeclaredField(this, "mLogging", true)
    return reflectLogging as? Printer
}

fun Long.dropCount(last: Long, devRefreshRate: Float): Int {
    val diffMs = TimeUnit.MILLISECONDS.convert((this - last), TimeUnit.NANOSECONDS)
    val dev = Math.round(devRefreshRate).toLong()
    return if (diffMs > dev) {
        val droppedCount = diffMs / dev
        droppedCount.toInt()
    } else {
        0
    }
}