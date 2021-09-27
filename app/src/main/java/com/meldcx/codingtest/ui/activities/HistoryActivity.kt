package com.meldcx.codingtest.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.meldcx.codingtest.R
import com.meldcx.codingtest.data.models.HistoryEntity
import com.meldcx.codingtest.databinding.ActivityHistoryBinding
import com.meldcx.codingtest.databinding.LayoutHistoryItemBinding
import com.meldcx.codingtest.service.appConstants.RETURN_INTENT_KEY
import com.meldcx.codingtest.service.listeners.CustomTextChangeListener
import com.meldcx.codingtest.ui.adapters.CommonRecyclerAdapter
import com.meldcx.codingtest.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File


/*
* HistoryActivity uses ExperimentalCoroutinesApi
*
* * INDICATE AS SECONDARY ACTIVITY
*
* Uses databinding layout the setContentview
* It has a recyclerView to show all the history items collected form database.
* @AndroidEntryPoint annotation added, so you can inject any dependencies.
* */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HistoryActivity : BaseActivity() {

    private lateinit var binding:ActivityHistoryBinding
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var historyAdapter: CommonRecyclerAdapter<HistoryEntity>

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        binding.root.post {
            buildUi()
            setObserver()
        }
    }


    private fun setObserver() {
        //Observer of List<HistoryEntity> comes from dirctViewModel
        viewModel.historyList.observe(this){baseData->
            if (baseData.isSuccess && !baseData.data.isNullOrEmpty())
                historyAdapter.listOfItems = baseData.data?.toMutableList()
        }
    }

    private fun buildUi() {

        //Set up commonAdapter of Type HistoryEntity
        historyAdapter = CommonRecyclerAdapter()
        historyAdapter.apply {
            expressionViewHolderBinding = { eachItem, viewBinding, _ ->
                (viewBinding as LayoutHistoryItemBinding).apply {
                    tvUrl.text = eachItem.url
                    tvDateTime.text = eachItem.createdAt
                    ivSnapShot.setImageBitmap(BitmapFactory.decodeFile(File(eachItem.imagePath).absolutePath))

                    btnDelete.setOnClickListener {
                        binding.layoutSearchView.etUrlInput.text.clear()
                        viewModel.deleteHistoryItem(eachItem)
                        viewModel.fetchAllHistories()
                    }
                    tvUrl.setOnClickListener {
                        val returnIntent = Intent()
                        returnIntent.putExtra(RETURN_INTENT_KEY, eachItem)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                    ivSnapShot.setOnClickListener {
                        val returnIntent = Intent()
                        returnIntent.putExtra(RETURN_INTENT_KEY, eachItem)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }
            expressionOnCreateViewHolder = {viewGroup ->
                DataBindingUtil.inflate<LayoutHistoryItemBinding>(LayoutInflater.from(viewGroup.context), R.layout.layout_history_item,viewGroup, false)
            }
        }

        binding.apply {
            //Simple custom textChange listener to filter items.
            //this will send to direct @Dao and will retrive filtered data list
            layoutSearchView.etUrlInput.addTextChangedListener(object :CustomTextChangeListener(){
                override fun afterTextChanged(p0: Editable?) {
                    super.afterTextChanged(p0)
                    viewModel.filterHistoryItem(p0.toString())
                }
            })
            rvHistory.layoutManager = LinearLayoutManager(this@HistoryActivity);
            rvHistory.adapter = historyAdapter
        }
        //Fetch all the data from app database
        lifecycleScope.launchWhenCreated {
            viewModel.fetchAllHistories()
        }
    }

    override fun getRootView() = binding.root

}