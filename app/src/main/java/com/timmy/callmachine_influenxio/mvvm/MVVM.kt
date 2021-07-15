package com.timmy.callmachine_influenxio.mvvm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.timmy.callmachine_influenxio.databinding.AdatperCounterBinding
import com.timmy.callmachine_influenxio.model.CounterModel

class VMFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}

@SuppressLint("StaticFieldLeak")
class MainViewModel() : ViewModel() {
    val listLiveData by lazy { MutableLiveData<ArrayList<CounterModel>>(ArrayList()) }
    val wattingList by lazy { MutableLiveData<ArrayList<Int>>() } // 必須是要LiveData主畫面才會更新 waitingsNumber
    var nowProcess: Int = 1
    var listener: ProcessListener? = null

    interface ProcessListener {
        fun process(position: Int)
        fun complete(position: Int)
    }

    init {
        wattingList.postValue(ArrayList())
        val list = ArrayList<CounterModel>()
        //櫃台名稱新增
        list.add(CounterModel("Amy"))
        list.add(CounterModel("Bob"))
        list.add(CounterModel("Cory"))
        list.add(CounterModel("Dora"))

        //指定處理CallBack
        list.forEachIndexed { index, counterModel ->
            counterModel.listener = object : CounterModel.ProcessListener {
                override fun process() {//通知MainActivity更新畫面
                    listener?.process(index)
                }

                override fun complete() {
                    processNext(wattingList.value ?: return) //找下一個去處理
                    listener?.complete(index)
                }
            }
        }
        listLiveData.postValue(list)
    }

    fun processNew(process: Int) {
        nowProcess++

        val list = wattingList.value ?: return
        list.add(process)
        wattingList.postValue(list)
        processNext(wattingList.value ?: return)
    }

    fun processNext(waitList: ArrayList<Int>) { // 這裡要找到有空的Counter去處理。

        listLiveData.value?.filter { it.nowProcess == "idle" }?.getOrNull(0)?.processing(waitList.getOrNull(0) ?: return) ?: return //如果沒有等候者或沒有空閒者則return。
        waitList.remove(waitList.getOrNull(0) ?: return) // 若沒有空閒者則不會執行到這裡
        wattingList.postValue(waitList)

    }
}

class CounterAdapter(val viewModel: MainViewModel) : RecyclerView.Adapter<CounterAdapter.ViewHolder>() {
    var list: ArrayList<CounterModel>? = viewModel.listLiveData.value
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list != null && list!!.isNotEmpty()) {
            val item = list?.get(position) ?: return
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    class ViewHolder private constructor(private val binding: AdatperCounterBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: CounterModel) {
            binding.cm = item
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AdatperCounterBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}