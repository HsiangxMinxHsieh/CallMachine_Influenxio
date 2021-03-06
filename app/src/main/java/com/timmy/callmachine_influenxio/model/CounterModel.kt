package com.timmy.callmachine_influenxio.model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData


data class CounterModel(
    val counterName: String = "",
    var nowProcess: String = "idle",
    val processTimeMin: Long = 500L,
    val processTimeMax: Long = 1500L,
    val hasProcessed: ArrayList<Int> = ArrayList(),
) {

    interface ProcessListener {
        fun process()
        fun complete()
    }

    private val processTime
        get() = (processTimeMin..processTimeMax).random()

    val hasProccedString by lazy { MutableLiveData<String>() }

    var listener: ProcessListener? = null

    fun processing(process: Int) {
        // 開始處理，處理中。
        nowProcess = process.toString()
        listener?.process()

        //處理完成
        Handler(Looper.myLooper() ?: return).postDelayed({
            hasProcessed.add(nowProcess.toInt())
            nowProcess = "idle"
            updateProccedString()
            listener?.complete()
        }, processTime)
    }

    private fun updateProccedString() {
        if (hasProcessed.size == 0)
            hasProccedString.postValue("")
        else {
            val s = hasProcessed.toString()
            hasProccedString.postValue(s.substring(1 until s.length - 1)) // [1,2,3]=>1,2,3
        }
    }
}