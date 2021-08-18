package com.masterbit.todoapp.common

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<T>(val data: T): UiState<T>()
    data class Error(val error: Int): UiState<Nothing>()
}