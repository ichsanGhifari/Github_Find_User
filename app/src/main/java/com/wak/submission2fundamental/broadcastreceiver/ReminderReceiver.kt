package com.wak.submission2fundamental.broadcastreceiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.wak.submission2fundamental.R
import com.wak.submission2fundamental.ui.main.MainActivity
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    companion object{
        const val TYPE_REPEATING = "reminder_repeating"
        private const val ID_REPEAT_REMINDER = 100
    }


    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context, ID_REPEAT_REMINDER)
    }
    fun setRepeatingReminder(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)// Set Alarm at 9AM
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEAT_REMINDER, intent, PendingIntent.FLAG_ONE_SHOT)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        Toast.makeText(context, context.getString(R.string.setupreminder) , Toast.LENGTH_SHORT).show()
    }
    fun cancelReminder (context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEAT_REMINDER, intent, 0)
        if(pendingIntent != null){
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
            Toast.makeText(context, context.getString(R.string.cancelreminder), Toast.LENGTH_SHORT).show()
        }
    }
    private fun showNotification(context: Context, notifId: Int) {
        val channelId = "wak205"
        val channelName = "Reminder Gitchannel"
        val intent =Intent(context,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0,intent,0)

        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.github)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notifMessage))
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }
}