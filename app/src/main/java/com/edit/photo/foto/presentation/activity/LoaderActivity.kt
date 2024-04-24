package com.edit.photo.foto.presentation.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edit.photo.foto.R
import com.edit.photo.foto.databinding.ActivityLoaderBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoaderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         requestPermissions(arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
        ),11111)
        val binding = ActivityLoaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val animation = AnimationUtils.loadAnimation(this@LoaderActivity, R.anim.animation)
        binding.imageView2.startAnimation(animation)
        lifecycleScope.launch {
            delay(2500)
            startActivity(Intent(this@LoaderActivity, MainActivity::class.java))
            finish()
        }
    }
}