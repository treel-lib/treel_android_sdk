package com.treel.androidsdkdemo.pushNotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.treel.androidsdkdemo.MainActivity
import com.treel.androidsdkdemo.R
import com.treel.androidsdk.notification.AlertNotification
import com.treel.androidsdk.utility.Constants.NOTIFICATION_CHANNEL_ID_DEFAULT
import timber.log.Timber
import java.util.*


/**
 * Class to display notifications.
 *
 * Created by Nitin Karande on 08/02/2018.
 */
class NotificationBuilder(
    context: Context,
    private val notificationManager: NotificationManager,
) {
    private val context: Context = context.applicationContext


    companion object {


        fun newInstance(context: Context): NotificationBuilder {
            val appContext = context.applicationContext
            var safeContext: Context? =
                ContextCompat.createDeviceProtectedStorageContext(appContext)
            if (safeContext == null) {
                safeContext = appContext
            }
            //val notificationManager = NotificationManagerCompat.from(safeContext)


            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return NotificationBuilder(
                safeContext!!,
                notificationManager
            )
        }
    }


    fun sendNotification(
        alertNotification: AlertNotification
    ) {
        // handle building and sending a normal notification
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_DEFAULT)
        //val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        // perform other configuration ...
        //builder.setContentTitle(alertNotification.getAlertsText())
        // set the group, this is important for later
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.setGroup(alertNotification.getUserNotificationGroup())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setTicker(alertNotification.getVinNumber())
            .setWhen(Calendar.getInstance().timeInMillis)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            .setContentTitle(alertNotification.getVinNumber())
            .setContentText(alertNotification.getAlertsText())
            .setStyle(NotificationCompat.BigTextStyle().bigText(alertNotification.getAlertsText()))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setContentIntent(pendingIntent)

        val builtNotification = builder.build()

        try {// deliver the standard notification
            val notificationId = Random().nextInt(20) + 1
            notificationManager.notify(
                alertNotification.getUserNotificationGroup(),
                notificationId,
                builtNotification
            )

            // pass our remote notification through to deliver a stack notification
            //sendStackNotificationIfNeeded(alertNotification)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannelForOreoAndAbove() {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var mChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID_DEFAULT)
        if (mChannel == null) {
            // Create the NotificationChannel
            val defaultSoundUri: Uri? =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val descriptionText = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            mChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID_DEFAULT,
                NOTIFICATION_CHANNEL_ID_DEFAULT,
                importance
            )
            mChannel.description = descriptionText
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            mChannel.setSound(defaultSoundUri, audioAttributes)
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.setShowBadge(true)
            mChannel.canShowBadge()
            notificationManager.createNotificationChannel(mChannel)
        }
    }

}