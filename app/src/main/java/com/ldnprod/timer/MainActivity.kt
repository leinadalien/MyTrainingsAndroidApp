package com.ldnprod.timer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ldnprod.timer.Dao.AppDatabase
import com.ldnprod.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getInstance(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.createButton.setOnClickListener {
            val intent = Intent(this, CreateTrainingActivity::class.java)
            startActivity(intent)
        }
    }
}