package com.meldcx.codingtest.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.meldcx.codingtest.R
import com.meldcx.codingtest.data.models.BaseDataModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/*
* A simple base view model class to handle the Flow comes from the repository
* It just takes a Flow, check wheather gets any exception and convert it to livedata
* flowHandler takes a liveData and a lamda,the lamda is the suspend function that gives the data from repository, and when data comes with or without exception,we just make a baseDataModel and emit it with viewModel
* FlowHandlerLive is the same but it just returns the livedata, rather then puting into the livedata
* */
abstract class BaseViewModel(private val application: Application): AndroidViewModel(application) {
    suspend fun <T>flowHandler(responseLiveData: MutableLiveData<BaseDataModel<T>>, flowValue:suspend ()-> Flow<T>) = viewModelScope.launch{
        flowValue.invoke()
            .catch { exception->
                responseLiveData.value = BaseDataModel(false,exception.message)
            }
            .collect { data->
                val baseData = BaseDataModel<T>(true,application.getString(R.string.collected_successfully))
                baseData.data = data
                responseLiveData.value = baseData
            }
    }
    fun <T>flowHandlerLiveData(flowValue:suspend ()-> Flow<T>):LiveData<BaseDataModel<T>> = liveData{
        flowValue.invoke()
            .catch { exception->
                emit(BaseDataModel<T>(false,exception.localizedMessage?:exception.message?:""))
            }.collect {data->
                val baseData = BaseDataModel<T>(true,application.getString(R.string.collected_successfully))
                baseData.data = data
                emit(baseData)
            }
    }
}