package es.samiralkalii.myapps.soporteit.framework.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.bumptech.glide.Glide
import es.samiralkalii.myapps.notification.INotification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import org.slf4j.LoggerFactory


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
const val KEY_TEXT_REPLY = "key_text_reply"

class NotificationManager(val context: Context): INotification {

    private val logger = LoggerFactory.getLogger(NotificationManager::class.java)

    override fun showNotificationBossUpdated(isBoss: Boolean) {

        val intent= if (isBoss) SplashActivity.getIntentToProfileScreen(context) else
            SplashActivity.getIntentForHome(context)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            val title= context.resources.getString(R.string.notif_title_boss_verification)
            val body= if (isBoss) context.resources.getString(R.string.notif_body_boss_verification_ok)
                else context.resources.getString(R.string.notif_body_boss_verification_ko)
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

    override fun showNotificationInvitationToTeam(bossName: String, bossMail: String, team: String, largeIconUrl: String, notificationId: String) {

        val intent= SplashActivity.getIntentToNotificationsScreen(context)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            val title= context.resources.getString(R.string.notif_title_invitation_to_be_part_of_team)
            val body= Html.fromHtml(context.resources.getString(R.string.notif_body_invitation_to_be_part_of_team, bossName, bossMail, team))
            val builder= NotificationCompat.Builder(context, context.getString(R.string.general_notif_channel_id))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            if (largeIconUrl.isNotBlank()) {
                val largeIconHeight = context.resources
                    .getDimensionPixelSize(android.R.dimen.notification_large_icon_height)
                val largeIconWidth = context.resources
                    .getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
                logger.debug("los valores de width y height es ${largeIconWidth},${largeIconHeight}")
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(largeIconUrl)
                    .into(largeIconWidth, largeIconHeight)
                    .get()
                builder.setLargeIcon(bitmap)
            }
            notify(NOTIF_ID, builder.build())
        }
    }

    override fun cancelNotification() {
        with(NotificationManagerCompat.from(context)) {
            //cancel(NOTIF_ID)
            cancelAll()
        }
    }


}