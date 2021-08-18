package com.masterbit.todoapp.data

import com.masterbit.todoapp.db.Task
import com.masterbit.todoapp.db.TaskDatabase
import com.masterbit.todoapp.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDatabase: TaskDatabase, @IODispatcher private val ioDispatcher: CoroutineDispatcher) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDatabase.taskDao().queryAllTasks()
    }

    fun getActiveTasks(): Flow<List<Task>> {
        return taskDatabase.taskDao().getActiveTasks()
    }

    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDatabase.taskDao().getCompletedTasks()
    }

    suspend fun getTask(taskId: Long): Task? {
        return withContext(ioDispatcher) {
            taskDatabase.taskDao().getTask(taskId)
        }
    }

    fun getTaskFlow(taskId: Long): Flow<Task> {
        return taskDatabase.taskDao().getTaskFlow(taskId)
    }

    suspend fun deleteCompletedTasks() {
        withContext(ioDispatcher){
            taskDatabase.taskDao().deleteCompletedTasks()
        }
    }

    suspend fun delete(task: Task) {
        withContext(ioDispatcher) {
            taskDatabase.taskDao().delete(task)
        }
    }

    suspend fun insertOrUpdateTask(task: Task) {
        withContext(ioDispatcher) {
            taskDatabase.taskDao().insert(task)
        }
    }
}