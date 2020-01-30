package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.R
import com.airbnb.epoxy.TypedEpoxyController
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.notificationItem

class NotificationsController: TypedEpoxyController<List<Notification>>() {

    override fun buildModels(data: List<Notification>?) {
        data?.forEach {
            notificationItem {
                id(it.id)
                notification(it)
            }
        }
    }
}