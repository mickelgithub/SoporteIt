package es.samiralkalii.myapps.domain.notification

import java.util.*

enum class NotifType {
    ACTION_INVITE_TEAM,
    REPLY,
    INFO,
    NONE
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

enum class Reply {
    NONE, OK, KO
}

data class Notification(
    var id: String= "",
    var type: NotifType= NotifType.NONE,
    var senderType: SenderType= SenderType.USER,
    var sender: String= "",
    var senderName: String= "",
    var senderEmail: String= "",
    var senderProfileImage: String= "",
    var sendDate: Long= Calendar.getInstance().time.time,
    var destinationType: DestinationType= DestinationType.USER,
    var destination: String= "",
    var team: String= "",
    var teamId: String= "",
    var state: NotifState= NotifState.PENDING,
    var deleted: Boolean= false,
    var deletionDate: Long= 0L,
    var reply: Reply= Reply.NONE,
    var replyDate: Long= 0L,
    var reasonKo: String= "") {

    companion object {
        val EMPTY= Notification()
    }
}