package com.masterbit.todoapp.common

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar

fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarLiveData: LiveData<Event<Int>>,
    timeout: Int
) {
    snackbarLiveData.observe(lifecycleOwner, EventObserver {
        Snackbar.make(this, it, timeout).show()
    })
}