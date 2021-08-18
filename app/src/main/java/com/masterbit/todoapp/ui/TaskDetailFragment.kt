package com.masterbit.todoapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import com.masterbit.todoapp.R
import com.masterbit.todoapp.common.Event
import com.masterbit.todoapp.common.UiState
import com.masterbit.todoapp.common.setupSnackbar
import com.masterbit.todoapp.databinding.FragmentTaskDetailBinding
import com.masterbit.todoapp.ui.AddEditTaskFragment.Companion.ADD_EDIT_TASK_BUNDLE_KEY
import com.masterbit.todoapp.ui.AddEditTaskFragment.Companion.ADD_EDIT_TASK_REQUEST_KEY
import com.masterbit.todoapp.viewmodels.AddEditTaskViewModel.Companion.TASK_ID_KEY
import com.masterbit.todoapp.viewmodels.TaskDetailViewModel
import com.masterbit.todoapp.viewmodels.TasksListViewModel.Companion.SNACKBAR_TIMEOUT
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    private lateinit var binding: FragmentTaskDetailBinding
    private val taskDetailViewModel by viewModels<TaskDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            clearFragmentResultListener(ADD_EDIT_TASK_REQUEST_KEY)
            setFragmentResultListener(ADD_EDIT_TASK_REQUEST_KEY) { _, bundle ->
                val result = bundle.getInt(ADD_EDIT_TASK_BUNDLE_KEY)
                taskDetailViewModel.responseOnMsg(result)
            }
            taskDetailViewModel.initialize(it.getLong(TASK_ID_KEY))
        } ?:run {
            setFragmentResult(TASK_DETAIL_KEY, bundleOf(TASK_DETAIL_MSG to Event(R.string.no_task_found)))
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.setupSnackbar(viewLifecycleOwner, taskDetailViewModel.snackbarLiveData, SNACKBAR_TIMEOUT)
        setupFab()
        taskDetailViewModel.taskLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    binding.checkMark.isChecked = it.data.completed
                    binding.checkMark.setOnCheckedChangeListener { _, isChecked ->
                        taskDetailViewModel.markTask(isChecked)
                    }
                    if (it.data.completed) {
                        val title = SpannableString(it.data.title)
                        title.setSpan(
                            StrikethroughSpan(),
                            0,
                            title.length,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        binding.title.text = title
                        it.data.description?.let {
                            val description = SpannableString(it)
                            description.setSpan(StrikethroughSpan(),
                            0,
                            description.length,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                            binding.description.text = description
                        }
                    } else {
                        binding.title.text = it.data.title
                        binding.description.text = it.data.description
                    }
                }
                is UiState.Error -> {

                }
            }
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val action = TaskDetailFragmentDirections.taskDetailToAddEditTask(taskDetailViewModel.task!!.id!!, getString(R.string.edit_Task))
            findNavController().navigate(action)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.trash -> {
                taskDetailViewModel.deleteTask()
                setFragmentResult(
                    TASK_DETAIL_KEY,
                    bundleOf(TASK_DETAIL_MSG to R.string.task_deleted)
                )
                findNavController().popBackStack()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        const val TASK_DETAIL_KEY = "task_detail_key"
        const val TASK_DETAIL_MSG = "task_detail_msg"
    }
}