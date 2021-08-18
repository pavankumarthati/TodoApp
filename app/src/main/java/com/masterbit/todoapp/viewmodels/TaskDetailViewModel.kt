package com.masterbit.todoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterbit.todoapp.R
import com.masterbit.todoapp.common.Event
import com.masterbit.todoapp.common.UiState
import com.masterbit.todoapp.data.TaskRepository
import com.masterbit.todoapp.db.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(private val taskRepository: TaskRepository): ViewModel() {

    private var taskId: Long? = null
    private var _taskLiveData = MutableLiveData<UiState<Task>>()
    val taskLiveData = _taskLiveData
    private var _snackbarLiveData = MutableLiveData<Event<Int>>()
    val snackbarLiveData = _snackbarLiveData
    var task: Task? = null
    private set


    fun initialize(taskId: Long) {
        this.taskId = taskId

        viewModelScope.launch {
            _taskLiveData.value = UiState.Loading
            val flow = taskRepository.getTaskFlow(taskId)
            flow.onCompletion { cause ->
                if (cause != null) {
                    _snackbarLiveData.value = Event(R.string.no_task_found)
                    _taskLiveData.value = UiState.Error(R.string.no_task_found)
                }
            }
            .collect {
                _taskLiveData.value = UiState.Success(it)
                this@TaskDetailViewModel.task = it
            }
        }
    }

    fun markTask(completed: Boolean) {
        task?.let {
            viewModelScope.launch {
                val task = it.copy(completed = completed)
                taskRepository.insertOrUpdateTask(task)
                this@TaskDetailViewModel.task = task
                _taskLiveData.value = UiState.Success(task)
                if (completed) {
                    _snackbarLiveData.value = Event(R.string.task_completed)
                } else {
                    _snackbarLiveData.value = Event(R.string.task_active)
                }
            }
        }
    }

    fun responseOnMsg(result: Int) {
        _snackbarLiveData.value = Event(result)
    }

    fun deleteTask() {
        task?.let {
            viewModelScope.launch {
                taskRepository.delete(it)
            }
        }
    }
}