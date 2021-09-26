package com.meldcx.codingtest.data.models

data class BaseDataModel<T> (
    var isSuccess:Boolean = false,
    var message:String? = null,
    var data:T? = null
)