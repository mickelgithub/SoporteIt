package es.samiralkalii.myapps.soporteit.framework.remotestorage.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.*
import messaging.RegisterMessagingTokenUseCase
import org.koin.android.ext.android.inject
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class MyFirebaseInstanceIDService() : CoroutineScope, FirebaseMessagingService() {

    private val logger = LoggerFactory.getLogger(MyFirebaseInstanceIDService::class.java)

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

    val registerMessagingTokenUseCase: RegisterMessagingTokenUseCase by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logger.debug("token: $token")

        CoroutineScope(coroutineContext).launch {
            val result = async(Dispatchers.IO) {
                sendRegistrationToServer(token)
            }.await()
        }


    }

    suspend private fun sendRegistrationToServer(token: String) {
        registerMessagingTokenUseCase(token)
    }
}