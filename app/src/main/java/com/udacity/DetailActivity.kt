package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import timber.log.Timber

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        val status = intent.getStringExtra("status")
        Timber.i("status: $status")
        val repository = intent.getStringExtra("repository")
        Timber.i("repository: $repository")

        textView2.text = repository

        val color = when (status) {
            getString(R.string.success) -> getColor(R.color.colorPrimaryDark)
            else -> Color.RED
        }

        textView4.setTextColor(color)
        textView4.text = status

        button.setOnClickListener {
            finish()
        }
    }

}
