package com.meldcx.codingtest.data.models

/*
* BaseDataModel is to check wheather the operation response is success or failed.
* In many case the app database may give some errors.
* So this with a base dataclass with generic type of data we can check the exceptions and get the proper messages.
* */
data class BaseDataModel<T> (
    var isSuccess:Boolean = false,
    var message:String? = null,
    var data:T? = null
)