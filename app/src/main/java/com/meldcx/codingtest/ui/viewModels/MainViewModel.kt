package com.meldcx.codingtest.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.meldcx.codingtest.data.dataRepository.HistoryRepository
import com.meldcx.codingtest.data.models.BaseDataModel
import com.meldcx.codingtest.data.models.HistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: HistoryRepository, application:Application):BaseViewModel(application) {

    private val _HistoryList = MutableLiveData<BaseDataModel<List<HistoryEntity>>>()
    val historyList:LiveData<BaseDataModel<List<HistoryEntity>>> = _HistoryList

    private var currentUrl:String? = null
    fun setCurrentUrl(url:String){
        this.currentUrl = url
    }
    fun getCurrentUrl() = currentUrl

    fun fetchAllHistories() = viewModelScope.launch{
        flowHandler(_HistoryList){repository.fetchAllHistories()}
    }

    fun insertHistoryItem(item:HistoryEntity) = viewModelScope.launch {
        repository.insertHistoryData(item)
    }

    fun deleteHistoryItem(item:HistoryEntity) = viewModelScope.launch {
        repository.deleteHistoryData(item)
    }

    fun updateHistoryItem(item:HistoryEntity) = viewModelScope.launch {
        repository.updateHistoryData(item)
    }

    fun filterHistoryItem(keyword:String) = viewModelScope.launch {
        flowHandler(_HistoryList){repository.fetchHistoriesWithKeywords(keyword)}
    }

    /*private val _SearchQueryChannel = Channel<String?>()

    fun sentToSearchQueryChannel(queryString: String?) = viewModelScope.launch {
        _SearchQueryChannel.send(queryString)
    }

    internal val querySearchResult:LiveData<BaseDataModel<List<HistoryEntity>>> = _SearchQueryChannel
        .receiveAsFlow()
        .debounce(300)
        .flatMapLatest {
            if (it.isNullOrEmpty()) {
                _HistoryList.asFlow()
            } else {
                flowHandlerLiveData { repository.fetchHistoriesWithKeywords(it) }.asFlow()
            }
        }.asLiveData()*/
}