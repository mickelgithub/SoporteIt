package es.samiralkalii.myapps.soporteit.framework.remotestorage.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.samiralkalii.myapps.usecase.messaging.RegisterMessagingTokenUseCase
import kotlinx.coroutines.*
import messaging.MESSAGE_BODY_KEY
import messaging.MESSAGE_ID_KEY
import messaging.NotifyMessagingUseCase
import messaging.RESULT_KEY
import org.koin.android.ext.android.inject
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext




class MyFirebaseInstanceIDService() : CoroutineScope, FirebaseMessagingService() {

    private val logger = LoggerFactory.getLogger(MyFirebaseInstanceIDService::class.java)

    val registerMessagingTokenUseCase: RegisterMessagingTokenUseCase by inject()
    val notifyMessagingUseCase: NotifyMessagingUseCase by inject()

    private val job= Job()

    override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

    override fun onCreate() {
        super.onCreate()
        logger.debug("onCreate....")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logger.debug("token(${Thread.currentThread().name}): $token")

        if (token.isNotBlank()) {
            launch(Dispatchers.Main) {
                logger.debug("inside main coroutine:${Thread.currentThread().name}")
                val result = async(Dispatchers.IO) {
                    logger.debug("inside coroutine IO (${Thread.currentThread().name})")
                    registerMessagingTokenUseCase(token)
                }.await()
            }
        }

    }

    override fun onMessageReceived(remoteMsg: RemoteMessage) {
        super.onMessageReceived(remoteMsg)
        with(remoteMsg.data) {
            val messageId= this[MESSAGE_ID_KEY] ?: ""
            val result= this[RESULT_KEY] ?: ""
            val body= this[MESSAGE_BODY_KEY] ?: ""
            notifyMessagingUseCase.invoke(messageId, result, body)
            logger.debug("the message received is $messageId -> $result")

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("onDestroy....")
    }
}