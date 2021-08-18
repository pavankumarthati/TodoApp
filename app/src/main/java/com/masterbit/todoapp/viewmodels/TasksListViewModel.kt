package com.masterbit.todoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterbit.todoapp.R
import com.masterbit.todoapp.common.Event
import com.masterbit.todoapp.common.Result
import com.masterbit.todoapp.common.TaskType
import com.masterbit.todoapp.data.TaskRepository
import com.masterbit.todoapp.db.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle, private val taskRepository: TaskRepository): ViewModel() {

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData = _loadingLiveData

    private val _snackbarLivedata = MutableLiveData<Event<Int>>()
    val snackbarLiveData = _snackbarLivedata

    private val _tasksLiveData = MutableLiveData<Result<List<Task>>>()
    val tasksLiveData = _tasksLiveData

    private val _openTaskLiveData = MutableLiveData<Event<Long>>()
    val openTaskLiveData = _openTaskLiveData

    private val _filterTypeLiveData = MutableLiveData<Int>()
    val filterTypeLiveData = _filterTypeLiveData
    private var currentFilter: TaskType? = null
    private var job: Job? = null

    init {
        currentFilter = savedStateHandle.get(TASK_TYPE_KEY)
    }

    fun setFilterType(taskType: TaskType) {
        savedStateHandle.set(TASK_TYPE_KEY, taskType)
        if (currentFilter == taskType) return
        currentFilter = taskType
        _filterTypeLiveData.value = when(currentFilter!!) {
            TaskType.ALL_TASKS -> {
                R.string.all_tasks
            }
            TaskType.COMPLETED_TASKS -> {
                R.string.completed_tasks
            }
            TaskType.ACTIVE_TASKS -> {
                R.string.active_tasks
            }
        }
        performRefresh()
    }

    fun performRefresh() {
        _loadingLiveData.value = true
        job?.cancel()
        job = viewModelScope.launch {
            when (currentFilter!!) {
                TaskType.ALL_TASKS -> {
                    taskRepository.getAllTasks()
                }
                TaskType.COMPLETED_TASKS -> {
                    taskRepository.getCompletedTasks()
                }
                TaskType.ACTIVE_TASKS -> {
                    taskRepository.getActiveTasks()
                }
            }
                .map {
                if (it.isNullOrEmpty()) {
                    snackbarLiveData.value = Event(R.string.no_results_found)
                    Result.Error(R.string.no_results_found)
                } else {
                    Result.Success(it)
                }
            }
                .collect {
                    _loadingLiveData.value = false
                    _tasksLiveData.value = it
            }
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            taskRepository.deleteCompletedTasks()
        }
    }

    fun openTask(taskId: Long) {
        _openTaskLiveData.value = Event(taskId)
    }

    fun completeTask(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val _task = task.copy(completed = isCompleted)
            taskRepository.insertOrUpdateTask(_task)
            if (isCompleted) {
                _snackbarLivedata.value = Event(R.string.task_completed)
            } else {
                _snackbarLivedata.value = Event(R.string.task_active)
            }
        }
    }

    fun respondToMsg(result: Int) {
        _snackbarLivedata.value = Event(result)
    }

    companion object {
        const val SNACKBAR_TIMEOUT = 500
        const val TASK_TYPE_KEY = "task_type_key"
    }

}