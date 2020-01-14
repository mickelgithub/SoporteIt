package es.samiralkalii.myapps.domain.notification

import java.util.*

enum class NotifType {
    ACTION_INVITE_TEAM,
    INFO
}

enum class SenderType {
    USER,
    PROGRAME
}

enum class DestinationType {
    USER,
    GROUP
}

enum class NotifState {
    PENDING,
    READ
}

data class Notification (
    var id: String,
    var type: NotifType,
    var senderType: SenderType= SenderType.USER,
    var sender: String,
    var sendDate: Long= Calendar.getInstance().time.time,
    var destinationType: DestinationType= DestinationType.USER,
    var destination: String,
    var team: String,
    var teamId: String,
    var state: NotifState= NotifState.PENDING,
    var deleted: Boolean= false
)