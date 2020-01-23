package es.samiralkalii.myapps.soporteit.framework.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import es.samiralkalii.myapps.notification.INotification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity


fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.general_notif_channel_name)
        //val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(context.getString(R.string.general_notif_channel_id), name, importance)
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

const val NOTIF_ID= 101

class NotificationManager(val context: Context): INotification {

    override fun showNotificationBossUpdated(title: String, body: String) {

        val intent= SplashActivity.getIntentToProfileScreen(context)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIF_ID, NotificationCompat.Builder(context, context.getString(R.string.general_notif_channel_id))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(body))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build())
        }
    }

    override fun showNotificationInvitationToTeam(title: String, body: String, largeIconUrl: String) {
        //intent when clicking notificaciont body
        //val intent= SplashActivity.getIntentToNotificationsScreen(context)
        //val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        //intent when clicking accept action
        val acceptInvitationIntent= SplashActivity.getIntentToHomeScreen(context, SplashActivity.REPLY_TEAM_INVITATION_OK)
        val acceptInvitacionPendingIntent= PendingIntent.getActivity(context, 0, acceptInvitationIntent, 0)


        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define

            if (largeIconUrl.isNotBlank()) {
                val bitmap= Glide.with(context)
                    .asBitmap()
                    .load(largeIconUrl)
                    .into(100, 100)
                    .get()
                notify(NOTIF_ID, NotificationCompat.Builder(context, context.getString(R.string.general_notif_channel_id))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(body))
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setContentIntent(pendingIntent)
                    .addAction(0, context.resources.getString(R.string.accept), acceptInvitacionPendingIntent)
                    .setAutoCancel(true).build())
            } else {
                notify(NOTIF_ID, NotificationCompat.Builder(context, context.getString(R.string.general_notif_channel_id))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(body))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setContentIntent(pendingIntent)
                    .addAction(0, context.resources.getString(R.string.accept), acceptInvitacionPendingIntent)
                    .setAutoCancel(true).build())
            }
        }
    }

    override fun cancelNotification() {
        with(NotificationManagerCompat.from(context)) {
            //cancel(NOTIF_ID)
            cancelAll()
        }
    }


}