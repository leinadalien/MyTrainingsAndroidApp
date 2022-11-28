package com.ldnprod.timer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ldnprod.timer.Adapters.TrainingAdapter
import com.ldnprod.timer.DI.MainModule
import com.ldnprod.timer.Dao.AppDatabase
import com.ldnprod.timer.Dao.TrainingDao
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Implementations.TrainingRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.ViewModels.TrainingListViewModel
import com.ldnprod.timer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.job

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<TrainingListViewModel>()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var adapter: TrainingAdapter
        viewModel.trainings.job.invokeOnCompletion { adapter = TrainingAdapter(it as List<Training>) }
        binding.createButton.setOnClickListener {
            val intent = Intent(this, CreateTrainingActivity::class.java)
            startActivity(intent)
        }
    }
}