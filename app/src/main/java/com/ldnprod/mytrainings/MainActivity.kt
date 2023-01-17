package com.ldnprod.mytrainings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ldnprod.mytrainings.Adapters.TrainingAdapter
import com.ldnprod.mytrainings.Utils.TrainingListEvent
import com.ldnprod.mytrainings.ViewModels.TrainingListViewModel.TrainingListViewModelEvent
import com.ldnprod.mytrainings.ViewModels.TrainingListViewModel.TrainingListViewModel
import com.ldnprod.mytrainings.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<TrainingListViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TrainingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = TrainingAdapter(viewModel.trainings) { viewModel.onEvent(it) }
        binding.apply {
            createButton.setOnClickListener {
                val intent = Intent(this@MainActivity, TrainingInfoActivity::class.java)
                startActivity(intent)
            }
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter
        }
        lifecycleScope.launch {
            viewModel.viewModelEvent.collect { event ->
                when(event) {
                    is TrainingListViewModelEvent.TrainingInserted -> {
                        adapter.notifyItemInserted(event.position)
                    }
                    is TrainingListViewModelEvent.TrainingChanged -> {
                        adapter.notifyItemChanged(event.position)
                    }
                    is TrainingListViewModelEvent.TrainingSetChanged -> {
                        adapter.trainings = event.trainings
                        adapter.notifyDataSetChanged()
                    }
                    is TrainingListViewModelEvent.TrainingRemoved ->
                        adapter.notifyItemRemoved(event.position)
                    is TrainingListViewModelEvent.TrainingOpened -> {
                        val intent = Intent(this@MainActivity, PlayTrainingActivity::class.java)
                        intent.putExtra("trainingId", event.training.id)
                        startActivity(intent)
                    }
                    is TrainingListViewModelEvent.TrainingInfoOpened -> {
                        val intent = Intent(this@MainActivity, TrainingInfoActivity::class.java)
                        intent.putExtra("trainingId", event.training.id)
                        startActivity(intent)
                    }
                    else -> Unit
                }
            }
        }
    }
    private lateinit var changeDarkModeButton: MenuItem
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        changeDarkModeButton = menu!!.findItem(R.id.change_theme_action)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.change_theme_action -> {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    changeDarkModeButton.setIcon(R.drawable.ic_dark_mode)
                    changeDarkModeButton.setTitle(R.string.dark_mode)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    changeDarkModeButton.setIcon(R.drawable.ic_light_mode)
                    changeDarkModeButton.setTitle(R.string.light_mode)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.onEvent(TrainingListEvent.OnRequestUpdatesForList)

    }
}