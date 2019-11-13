package es.samiralkalii.myapps.soporteit.framework.remotestorage.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import es.samiralkalii.myapps.usecase.messaging.RegisterMessagingTokenUseCase
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

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

    suspend private fun sendRegistrationToServer(token: String) {
        registerMessagingTokenUseCase(token)
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("onDestroy....")
    }
}