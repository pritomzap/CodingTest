package com.meldcx.codingtest.service.listeners

import android.text.Editable
import android.text.TextWatcher


/*
* A custom textWathcer class.
* By using this class insted of direct textWatcher,we can minimize override funtions
* */
abstract class CustomTextChangeListener:TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {

    }
}