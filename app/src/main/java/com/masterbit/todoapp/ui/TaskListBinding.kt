package com.masterbit.todoapp.ui

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:completedTask")
fun setStyle(view: TextView, enabled: Boolean) {
    if (enabled) {
        view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        view.paintFlags = view.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}