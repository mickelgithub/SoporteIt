package es.samiralkalii.myapps.soporteit.framework.remotestorage.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.samiralkalii.myapps.usecase.messaging.RegisterMessagingTokenUseCase
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

private const val RESULT_OK_VALUE= "OK"
private const val RESULT_KO_VALUE= "KO"
private const val MESSAGE_ID_KEY= "messageId"
private const val RESULT_KEY= "result"


class MyFirebaseInstanceIDService() : CoroutineScope, FirebaseMessagingService() {

    private val logger = LoggerFactory.getLogger(MyFirebaseInstanceIDService::class.java)

    private val job= Job()

    override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

    override fun onCreate() {
        super.onCreate()
        logger.debug("onCreate....")
    }

    val registerMessagingTokenUseCase: RegisterMessagingTokenUseCase by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logger.debug("token(${Thread.currentThread().name}): $token")

        launch(Dispatchers.Main) {
            logger.debug("inside main coroutine:${Thread.currentThread().name}")
            val result = async(Dispatchers.IO) {
                logger.debug("inside coroutine IO (${Thread.currentThread().name})")
                sendRegistrationToServer(token)
            }.await()
        }
    }

    override fun onMessageReceived(remoteMsg: RemoteMessage) {
        super.onMessageReceived(remoteMsg)
        with(remoteMsg.data) {
            val messageId= this[MESSAGE_ID_KEY] ?: ""
            val result= this[RESULT_KEY] ?: ""
            logger.debug("the message received is $messageId -> $result")

        }

    }

    suspend private fun sendRegistrationToServer(token: String) {
        registerMessagingTokenUseCase(token)
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("onDestroy....")
    }
}