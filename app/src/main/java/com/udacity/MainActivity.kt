package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var loadResource: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (loadResource == null) {
                Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            custom_button.setState(ButtonState.Clicked)
            download()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            loadResource = when (checkedId) {
                R.id.radioButton1 -> glideURL
                R.id.radioButton2 -> loadURL
                R.id.radioButton3 -> retrofitURL
                else -> null
            }

            Timber.i("loadResource: $loadResource")
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) != downloadID) {
                return
            }

            custom_button.setState(ButtonState.Completed)

            val query = DownloadManager.Query()
            query.setFilterById(downloadID)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val detailIntent = Intent(context, DetailActivity::class.java)

            downloadManager.query(query).use { cursor ->
                if (cursor.moveToFirst()) {
                    val status = when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.success)
                        else -> getString(R.string.fail)
                    }

                    Timber.i("onReceive status: $status")
                    detailIntent.putExtra("status", status)
                }
            }

            if (context == null) return



            val pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val action = NotificationCompat.Action(
                R.drawable.ic_assistant_black_24dp,
                context.getString(R.string.check_the_status),
                pendingIntent
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_description))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_LOW)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun download() {
        if (loadResource == null) return

        val request =
            DownloadManager.Request(Uri.parse(loadResource))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // enqueue puts the download request in the queue.
        downloadID = downloadManager.enqueue(request)
    }

    companion object {
        private const val glideURL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val loadURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val retrofitURL = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "Downloads"
        private const val NOTIFICATION_ID = 0
    }

}
