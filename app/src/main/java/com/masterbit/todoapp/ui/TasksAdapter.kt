package com.masterbit.todoapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.masterbit.todoapp.databinding.TaskItemViewBinding
import com.masterbit.todoapp.db.Task
import com.masterbit.todoapp.viewmodels.TasksListViewModel

class TasksAdapter(private val tasksListViewModel: TasksListViewModel): ListAdapter<Task, TasksAdapter.TaskItemViewHolder>(DiffItemCallback()) {


    class TaskItemViewHolder(private val binding: TaskItemViewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(listViewModel: TasksListViewModel, task: Task) {
            binding.viewmodel = listViewModel
            binding.task = task
            binding.executePendingBindings()
        }

        companion object {

            fun create(parent: ViewGroup): TaskItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = TaskItemViewBinding.inflate(inflater, parent, false)
                return TaskItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        return TaskItemViewHolder.create(parent = parent)
    }

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bind(tasksListViewModel, getItem(position))
    }

}

class DiffItemCallback: DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

}