package com.masterbit.todoapp.ui

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.masterbit.todoapp.R
import com.masterbit.todoapp.common.EventObserver
import com.masterbit.todoapp.common.Result
import com.masterbit.todoapp.common.TaskType
import com.masterbit.todoapp.common.setupSnackbar
import com.masterbit.todoapp.databinding.FragmentTaskListBinding
import com.masterbit.todoapp.ui.AddEditTaskFragment.Companion.ADD_EDIT_TASK_BUNDLE_KEY
import com.masterbit.todoapp.ui.AddEditTaskFragment.Companion.ADD_EDIT_TASK_REQUEST_KEY
import com.masterbit.todoapp.ui.TaskDetailFragment.Companion.TASK_DETAIL_KEY
import com.masterbit.todoapp.ui.TaskDetailFragment.Companion.TASK_DETAIL_MSG
import com.masterbit.todoapp.viewmodels.TasksListViewModel
import com.masterbit.todoapp.viewmodels.TasksListViewModel.Companion.SNACKBAR_TIMEOUT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListScreenFragment: Fragment() {
    private val tasksViewModel by viewModels<TasksListViewModel>()
    private lateinit var binding: FragmentTaskListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (savedInstanceState == null) {
            arguments?.let {
                tasksViewModel.setFilterType(it.getSerializable("taskType") as TaskType)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskListBinding.inflate(inflater)
        binding.taskList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.taskList.adapter = TasksAdapter(tasksViewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRefreshLayout()
        setupSnackbar()
        setupFab()
        tasksViewModel.filterTypeLiveData.observe(viewLifecycleOwner) {
            binding.taskTypeLabel.text = getText(it)
        }

        tasksViewModel.openTaskLiveData.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })

        tasksViewModel.tasksLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    binding.taskList.isVisible = true
                    binding.error.isVisible = false
                    (binding.taskList.adapter as TasksAdapter).submitList(it.data)
                }
                is Result.Error -> {
                    binding.taskList.isVisible = false
                    binding.error.isVisible = true
                    binding.error.text = getText(it.error)
                }
            }
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            clearFragmentResultListener(ADD_EDIT_TASK_REQUEST_KEY)
            setFragmentResultListener(AddEditTaskFragment.ADD_EDIT_TASK_REQUEST_KEY) { _, bundle ->
                val result = bundle.getInt(ADD_EDIT_TASK_BUNDLE_KEY)
                tasksViewModel.respondToMsg(result)
            }
            val action = TaskListScreenFragmentDirections.taskListToAddEditScreen(-1L, getString(R.string.add_task_label))
            findNavController().navigate(action)
        }
    }

    private fun openTaskDetails(taskId: Long) {
        clearFragmentResultListener(TASK_DETAIL_KEY)
        setFragmentResultListener(TASK_DETAIL_KEY) { _, bundle ->
            val result = bundle.getInt(TASK_DETAIL_MSG)
            tasksViewModel.respondToMsg(result)
        }
        val action = TaskListScreenFragmentDirections.taskListToTaskDetailFragment(taskId)
        findNavController().navigate(action)
    }

    private fun setupRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            tasksViewModel.performRefresh()
        }
        tasksViewModel.loadingLiveData.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = it
        }
    }

    private fun setupSnackbar() {
        binding.root.setupSnackbar(viewLifecycleOwner, tasksViewModel.snackbarLiveData, SNACKBAR_TIMEOUT)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.task_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                tasksViewModel.performRefresh()
                true
            }
            R.id.clear -> {
                tasksViewModel.clearCompleted()
                true
            }
            R.id.filter -> {
                showFilteringPopUpMenu()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = requireActivity().findViewById<View>(R.id.filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_types, menu)
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.active -> {
                        tasksViewModel.setFilterType(TaskType.ACTIVE_TASKS)
                    }
                    R.id.completed -> {
                        tasksViewModel.setFilterType(TaskType.COMPLETED_TASKS)
                    }
                    else -> {
                        tasksViewModel.setFilterType(TaskType.ALL_TASKS)
                    }
                }
                true
            }
            show()
        }
    }
}