package ngwaikong.com.jankmonitor.core

import android.util.Printer

/**
 * Created by weijiangwu on 2018/3/27.
 */
class BasePrinter : Printer {
    private val printerList: ArrayList<Printer> = ArrayList()

    override fun println(log: String?) {
        printerList.forEach {
            it.println(log)
        }
    }

    fun addPrinter(printer: Printer) {
        printerList.add(printer)
    }

    fun removePrinter(printer: Printer) {
        printerList.remove(printer)
    }

}