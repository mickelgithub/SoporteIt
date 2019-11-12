package es.samiralkalii.myapps.soporteit.framework.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import org.slf4j.LoggerFactory

class MyFirebaseInstanceIDService: FirebaseMessagingService() {

    private val logger = LoggerFactory.getLogger(MyFirebaseInstanceIDService::class.java)

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        logger.debug("token: $p0")
    }
}