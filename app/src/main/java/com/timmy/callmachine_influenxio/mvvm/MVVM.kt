package com.timmy.callmachine_influenxio.mvvm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.timmy.callmachine_influenxio.databinding.AdatperCounterBinding
import com.timmy.callmachine_influenxio.model.CounterModel
import com.timmy.callmachine_influenxio.util.logi

class VMFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}

@SuppressLint("StaticFieldLeak")
class MainViewModel() : ViewModel() {
    val TAG = javaClass.simpleName
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
        list.add(CounterModel(0, "Amy"))
        list.add(CounterModel(1, "Bob"))
        list.add(CounterModel(2, "Cory"))
        list.add(CounterModel(3, "Dora"))
        list.forEachIndexed { index, counterModel ->
            counterModel.listener = object : CounterModel.ProcessListener {
                override fun process() {//通知MainActivity更新畫面
                    listener?.process(index)
                }

                override fun complete(counterModel: CounterModel, process: Int) {
                    processNext(wattingList.value ?: return)
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

        listLiveData.value?.filter { it.nowProcess == "idle" }?.getOrNull(0)?.processing(waitList.getOrNull(0) ?: return) ?: return

        waitList.remove(waitList.getOrNull(0) ?: return)
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