package com.meldcx.codingtest.ui.customViews

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.meldcx.codingtest.R
import com.meldcx.codingtest.databinding.CustomProgressDialogBinding




object CustomProgressDialog {

    private lateinit var customProgressDialog: Dialog

    fun show(activity: Activity) {
        customProgressDialog = Dialog(activity)
        customProgressDialog.window!!
            .setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        activity,
                        android.R.color.transparent
                    )
                )
            )
        customProgressDialog.setCancelable(false)
        customProgressDialog.setCanceledOnTouchOutside(false)
        val progressDialogBinding: CustomProgressDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.custom_progress_dialog,
            null,
            false
        )
        customProgressDialog.setContentView(progressDialogBinding.getRoot())
        customProgressDialog.show()
    }

    fun dismiss() {
        try {
            if (customProgressDialog.isShowing) customProgressDialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isShowing(): Boolean {
        return customProgressDialog.isShowing
    }

    fun setCancelable(status: Boolean) {
        customProgressDialog.setCancelable(status)
    }
}