package es.samiralkalii.myapps.domain

data class User(var email: String= "", var password: String= "",
                var name: String= "",
                var id: String= "",
                var localProfileImage: String= "",
                var remoteProfileImage: String= "",
                var creationDate: Long= 0,
                var emailVerified: Boolean= false) {

    companion object {
        val Empty= User()
    }


}



