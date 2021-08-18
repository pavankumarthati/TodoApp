package com.masterbit.todoapp.viewmodels

import androidx.lifecycle.*
import com.masterbit.todoapp.R
import com.masterbit.todoapp.common.Event
import com.masterbit.todoapp.data.TaskRepository
import com.masterbit.todoapp.db.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    private var taskId: Long? = null
    private var isNewTask: Boolean = true

    private val _taskLiveData = MutableLiveData<Task>()
    val taskLiveData = _taskLiveData

    private val _snackbarLiveData = MutableLiveData<Event<Int>>()
    val snackbarLiveData = _snackbarLiveData

    private val _taskValidationLiveData = MutableLiveData<Event<Int>>()
    val taskValidationLiveData = _taskValidationLiveData

    private var _channel: SendChannel<Pair<String, String>>? = null

    fun initialize(taskId: Long) {
        if (taskId == -1L) return
        this.taskId = taskId
        isNewTask = false

        viewModelScope.launch {
            val task = taskRepository.getTask(taskId)
            if (task == null) {
                isNewTask = true
                _snackbarLiveData.value = Event(R.string.no_task_found)
            } else {
                _taskLiveData.value = task.copy(task.title, task.description, task.completed, task.id)

            }
        }
    }

    fun onDone(title: String, description: String): Int {
        val task = if (isNewTask) {
            Task(title, description, false)
        } else {
            Task(title, description, _taskLiveData.value!!.completed, _taskLiveData.value!!.id!!)
        }
        viewModelScope.launch {
            taskRepository.insertOrUpdateTask(task)
        }
        return if (isNewTask) {
            R.string.task_added_msg
        } else {
            R.string.task_updated_msg
        }
    }

    @ExperimentalCoroutinesApi
    fun onTextFieldsChanged(a: suspend ((Pair<String, String>) -> Unit) -> Unit) {
        viewModelScope.launch {
            callbackFlow<Pair<String, String>> {
                val pScope = this
                _channel = this.channel
                a.invoke {
                    pScope.launch {
                        _channel?.send(it)
                    }
                }
                awaitClose { }
            }
                .debounce(300)
                .collect {
                    if (it.first.isNullOrEmpty() && it.second.length > 100) {
                        _taskValidationLiveData.value = Event(0)
                        _snackbarLiveData.value = Event(R.string.fields_not_empty)
                    } else if (it.first.isNullOrEmpty()) {
                        _taskValidationLiveData.value = Event(2)
                        _snackbarLiveData.value = Event(R.string.title_empty)
                    } else if (it.second.length > 100) {
                        _taskValidationLiveData.value = Event(1)
                        _snackbarLiveData.value = Event(R.string.description_not_valid)
                    } else {
                        _taskValidationLiveData.value = Event(3)
                    }
                }
        }
    }

    fun onViewCleared() {
        _channel?.close()
    }

    companion object {
        const val TASK_ID_KEY = "task_id_key"
    }

}