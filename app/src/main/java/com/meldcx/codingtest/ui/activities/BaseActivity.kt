package com.meldcx.codingtest.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

/**
 * Created by Sahidul Islam on 20-Jul-21.
 */
abstract class BaseActivity : AppCompatActivity() {

    var keyboardListenersAttached = false
    var displayMetrics: DisplayMetrics? = null
    var savedInstanceState: Bundle? = null

    abstract fun initView()

    abstract fun getRootView(): View

    override fun onCreate(savedInstanceState: Bundle?) {
        initConfig()
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        this.savedInstanceState = savedInstanceState
        displayMetrics = DisplayMetrics()
        (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        initView()
    }

    open fun initConfig() {}

    override fun onDestroy() {
        super.onDestroy()
        if (keyboardListenersAttached) {
            getRootView().viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val heightDiff =
            getRootView().rootView.height - getRootView().height
        val contentViewTop =
            window.findViewById<View>(Window.ID_ANDROID_CONTENT)
                .top
        if (contentViewTop > 0) {
            if (heightDiff <= contentViewTop) {
                onHideKeyboard()
            } else {
                onShowKeyboard()
            }
        }
    }

    fun hasPermissions(permissionList:List<String>):List<String>{
        val unAcceptedPermissions = mutableListOf<String>()
        permissionList.forEach {
            if (ContextCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED)
                unAcceptedPermissions.add(it)
        }
        return unAcceptedPermissions
    }

    open fun onShowKeyboard() {}

    open fun onHideKeyboard() {}

    protected open fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }
        getRootView().viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        keyboardListenersAttached = true
    }


}