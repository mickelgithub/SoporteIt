package es.samiralkalii.myapps.domain

import es.samiralkalii.myapps.domain.notification.Reply

data class User(var email: String= "", var password: String= "",
                var name: String= "",
                var id: String= "",
                var localProfileImage: String= "",
                var remoteProfileImage: String= "",
                var creationDate: Long= 0,
                var emailVerified: Boolean= false,
                var profile: String= "",
                var bossVerification: String= "",
                var team: String= "",
                var teamId: String= "",
                var teamInvitationState: Reply = Reply.NONE,
                var boss: String= "",
                var messagingToken: String= "",
                var holidayDaysPerYear: Long= DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS,
                var internalEmployee: Boolean= true
                ) {

    companion object {
        val EMPTY= User()
        val DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS= 22L
        val DEFAULT_HOLIDAY_DAYS_FOR_INTERNALS= 26L
    }

    fun copy(user: User) {
        this.email= user.email
        this.password= user.password
        this.name= user.name
        this.id= user.id
        this.localProfileImage= user.localProfileImage
        this.remoteProfileImage= user.remoteProfileImage
        this.creationDate= user.creationDate
        this.emailVerified= user.emailVerified
        this.profile= user.profile
        this.bossVerification= user.bossVerification
        this.team= user.team
        this.teamId= user.teamId
        this.teamInvitationState= user.teamInvitationState
        this.boss= user.boss
        this.messagingToken= user.messagingToken
        this.holidayDaysPerYear= user.holidayDaysPerYear
        this.internalEmployee= user.internalEmployee
    }


}



