package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.util.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private var downloadStatus = ""
    private var fileSource = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        downloadStatus = intent.getStringExtra("status").toString()
        fileSource = intent.getStringExtra("fileSource").toString()

        file_name.text = fileSource
        status.text = downloadStatus

        button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        notificationManager.cancelNotifications()
    }

}
