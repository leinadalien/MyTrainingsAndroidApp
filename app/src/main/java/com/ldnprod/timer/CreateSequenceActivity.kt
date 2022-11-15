package com.ldnprod.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ldnprod.timer.databinding.ActivityCreateSequenceBinding

class CreateSequenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSequenceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSequenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.createButton.setOnClickListener {

        }
    }
}