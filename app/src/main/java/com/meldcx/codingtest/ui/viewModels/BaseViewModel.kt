package com.meldcx.codingtest.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.meldcx.codingtest.R
import com.meldcx.codingtest.data.models.BaseDataModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


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