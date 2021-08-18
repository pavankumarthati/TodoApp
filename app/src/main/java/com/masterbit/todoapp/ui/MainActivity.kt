package com.masterbit.todoapp.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.masterbit.todoapp.R
import com.masterbit.todoapp.common.TaskType
import com.masterbit.todoapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private val navController by lazy {
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        fragment.navController.apply {
            setGraph(R.navigation.nav_graph, bundleOf(Pair("taskType", TaskType.ALL_TASKS)))
        }
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            setupNavigation()
        }
    }

    private fun setupNavigation() {
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}