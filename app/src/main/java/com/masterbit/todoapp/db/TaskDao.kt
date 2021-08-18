package com.masterbit.todoapp.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("SELECT * FROM tasks WHERE id=:taskId")
    suspend fun getTask(taskId: Long): Task?

    @Query("SELECT * FROM tasks WHERE id=:taskId")
    fun getTaskFlow(taskId: Long): Flow<Task>

    @Query("SELECT * FROM tasks")
    fun queryAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE completed = 1")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE completed = 0")
    fun getActiveTasks(): Flow<List<Task>>

    @Query("DELETE FROM tasks WHERE completed = 1")
    suspend fun deleteCompletedTasks()

    @Delete
    fun delete(task: Task)
}