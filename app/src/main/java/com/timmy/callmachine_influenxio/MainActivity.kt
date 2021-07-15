package com.timmy.callmachine_influenxio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.timmy.callmachine_influenxio.mvvm.MainViewModel
import com.timmy.callmachine_influenxio.mvvm.VMFactory
import com.timmy.callmachine_influenxio.databinding.ActivityMainBinding
import com.timmy.callmachine_influenxio.mvvm.CounterAdapter

class MainActivity : AppCompatActivity() {
    private val activity = this
    lateinit var viewModel: MainViewModel
    lateinit var adapter: CounterAdapter
    lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)
        initView()

        initObserver()

        initEvent()
    }

    private fun initView() {
        mBinding.lifecycleOwner = activity
        mBinding.vm = ViewModelProvider(this, VMFactory()).get(MainViewModel::class.java)
        viewModel = mBinding.vm as MainViewModel

        adapter = CounterAdapter(viewModel = mBinding.vm as MainViewModel)

        mBinding.btnNext.text = activity.getString(R.string.button_text).format(viewModel.nowProcess)
        mBinding.rvCounterList.adapter = adapter
    }

    private fun initObserver() {
        viewModel.listLiveData.observe(this, {
            adapter.list = it
            adapter.notifyDataSetChanged()
        })
    }

    private fun initEvent() {
        mBinding.btnNext.setOnClickListener {

            viewModel.processNew(viewModel.nowProcess)
            mBinding.btnNext.text = activity.getString(R.string.button_text).format(viewModel.nowProcess)
        }

        viewModel.listener = object : MainViewModel.ProcessListener {
            override fun process(position: Int) {
                adapter.notifyItemChanged(position)
            }

            override fun complete(position: Int) {
                adapter.notifyItemChanged(position)
            }
        }
    }
}