package com.masterbit.todoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.masterbit.todoapp.R
import com.masterbit.todoapp.common.Event
import com.masterbit.todoapp.common.EventObserver
import com.masterbit.todoapp.common.setupSnackbar
import com.masterbit.todoapp.databinding.FragmentAddEditTaskBinding
import com.masterbit.todoapp.viewmodels.AddEditTaskViewModel
import com.masterbit.todoapp.viewmodels.AddEditTaskViewModel.Companion.TASK_ID_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class AddEditTaskFragment : Fragment() {

    private val addEditTaskViewModel by viewModels<AddEditTaskViewModel>()
    private lateinit var binding: FragmentAddEditTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            arguments?.getLong(TASK_ID_KEY)?.let {
                addEditTaskViewModel.initialize(it)
            } ?: run {
                setFragmentResult(ADD_EDIT_TASK_REQUEST_KEY, bundleOf(ADD_EDIT_TASK_BUNDLE_KEY to Event(R.string.no_task_found)))
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTextFields()
        setupFab()
        setupSnackbar()
    }

    @ExperimentalCoroutinesApi
    private fun setupTextFields() {
        addEditTaskViewModel.onTextFieldsChanged { block ->
            binding.titleEt.addTextChangedListener {
                val description = binding.descriptionEt.text?.toString() ?: ""
                val title = it?.toString() ?: ""
                block.invoke(title to description)
            }

            binding.descriptionEt.addTextChangedListener {
                val title = binding.titleEt.text?.toString() ?: ""
                val description = it?.toString() ?: ""
                block.invoke(title to description)
            }
        }

        addEditTaskViewModel.taskLiveData.observe(viewLifecycleOwner) {
            binding.fab.isEnabled = true
            binding.titleEt.setText(it.title)
            binding.descriptionEt.setText(it.description)
        }

        addEditTaskViewModel.taskValidationLiveData.observe(viewLifecycleOwner, EventObserver {
            binding.fab.isEnabled = (it == 3)
            binding.titleInputLayout.error = null
            binding.descriptionInputLayout.error = null
            if (it == 2) {
                binding.titleInputLayout.error = getString(R.string.title_empty)
            } else if (it == 1) {
                binding.descriptionInputLayout.error = getString(R.string.description_not_valid)
            } else if (it == 0) {
                binding.titleInputLayout.error = getString(R.string.title_empty)
                binding.descriptionInputLayout.error = getString(R.string.description_not_valid)
            }
        })
    }

    private fun setupSnackbar() {
        binding.root.setupSnackbar(viewLifecycleOwner, addEditTaskViewModel.snackbarLiveData, 2000)
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val result = addEditTaskViewModel.onDone(binding.titleEt.text?.toString() ?: "", binding.descriptionEt.text.toString() ?: "")
            setFragmentResult(ADD_EDIT_TASK_REQUEST_KEY, bundleOf(ADD_EDIT_TASK_BUNDLE_KEY to result))
            binding.root.findNavController().popBackStack()
        }

        binding.taskScrollableContainer?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fab.hide();
            } else {
                binding.fab.show();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addEditTaskViewModel.onViewCleared()
    }

    companion object {
        const val ADD_EDIT_TASK_REQUEST_KEY = "add_edit_request_key"
        const val ADD_EDIT_TASK_BUNDLE_KEY = "add_edit_bundle_key"
    }
}