package com.example.workmanager_xml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import com.example.workmanager_xml.Constants.Companion.notificationType
import com.example.workmanager_xml.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        binding?.uiBtnOneTimeTask?.setOnClickListener {
            notificationType = 1
            myOneTimeTask()
        }
        binding?.uiBtnPeriodicTask15?.setOnClickListener {
            notificationType = 15
            myPeriodicWork(15)
        }
        binding?.uiBtnPeriodicTask30?.setOnClickListener {
            notificationType = 30
            myPeriodicWork(20)
        }

    }

    private fun myOneTimeTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(true)
            .build()

        val myWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)
    }

    private fun myPeriodicWork(requestTime: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val myRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, requestTime, TimeUnit.MINUTES)
                // W/WM-WorkSpec: Interval duration lesser than minimum allowed value; Changed to 900000
                // -> default min interval is 15 minutes for battery optimization, changing this to less than 15m gets revert back to 15m
            .setConstraints(constraints)
            .addTag("myId")
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "myId",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                myRequest // -> The task will continue to run at the specified interval until you cancel it or the app is uninstalled.
            )
    }
}