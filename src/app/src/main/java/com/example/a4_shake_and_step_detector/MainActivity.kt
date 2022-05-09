package com.example.a4_shake_and_step_detector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a4_shake_and_step_detector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnUDM.setOnClickListener {
            val intentUDM = Intent(this, UserDefineMode::class.java)
            startActivity(intentUDM)
        }

        binding.btnFM.setOnClickListener {
            val intentFM = Intent(this, FreeMode::class.java)
            startActivity(intentFM)
        }

    }
}