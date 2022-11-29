package com.ldnprod.timer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ldnprod.timer.Adapters.TrainingAdapter
import com.ldnprod.timer.ViewModels.TrainingListViewModel.TrainingListViewModelEvent
import com.ldnprod.timer.ViewModels.TrainingListViewModel.TrainingListViewModel
import com.ldnprod.timer.databinding.ActivityMainBinding
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
        var adapter: TrainingAdapter

        binding.createButton.setOnClickListener {
            val intent = Intent(this, CreateTrainingActivity::class.java)
            startActivity(intent)
        }
//        lifecycleScope.launch {
//            viewModel.viewModelEvent.collect { event ->
//                when(event) {
//                    is TrainingListViewModelEvent.ItemInserted -> {
//                        //adapter.notifyItemInserted(event.position)
//                    }
//                    else -> Unit
//                }
//            }
//        }
    }
}