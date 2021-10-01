package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadSource:String? = null
    private var downloadSourceFile:String = "Source Unknown"
    private var isAnyOptionChosen = false
    private var downloadStatus = "Unknown"

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.checkIsOptionChosen(isAnyOptionChosen)
            download()
        }

    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager : DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            if (downloadID == id){
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                if (cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_FAILED -> {
                            downloadStatus = "Fail"
                            notificationManager.sendNotification(
                                getString(R.string.notification_description),
                                applicationContext,
                                downloadStatus,
                                downloadSource
                            )
                            custom_button.setState(ButtonState.Completed)
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            downloadStatus = "Success"
                            notificationManager.sendNotification(
                                getString(R.string.notification_description),
                                applicationContext,
                                downloadStatus,
                                downloadSourceFile
                            )
                            custom_button.setState(ButtonState.Completed)
                        }
                        else -> {
                            downloadStatus = "Unknown"
                        }
                    }
                }
                cursor.close()
            }
        }
    }

    @SuppressLint("Range")
    private fun download() {
        if (downloadSource != null) {
            downloadStatus = "Unknown"
            notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
            createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))
            val request =
                DownloadManager.Request(Uri.parse(downloadSource))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            custom_button.setState(ButtonState.Loading)
        }else{
            custom_button.setState(ButtonState.Completed)
            Toast.makeText(this, R.string.select_repo, Toast.LENGTH_SHORT).show()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            isAnyOptionChosen = true
            val checked = view.isChecked
            when (view.getId()) {
                R.id.glide ->
                    if (checked) {
                        downloadSource = glideURL
                        downloadSourceFile = getString(R.string.glide_radio_button)
                    }
                R.id.loadApp ->
                    if (checked) {
                        downloadSource = URL
                        downloadSourceFile = getString(R.string.load_app_radio_button)
                    }
                R.id.retrofit ->
                    if (checked) {
                        downloadSource = retrofitURL
                        downloadSourceFile = getString(R.string.retrofit_radio_button)
                    }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val glideURL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val retrofitURL =
            "https://github.com/square/retrofit/archive/master.zip"
    }

}
