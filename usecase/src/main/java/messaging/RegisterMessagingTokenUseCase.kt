package messaging

import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class RegisterMessagingTokenUseCase(private val preferenceRepository: PreferenceRepository,
                                    private val remoteUserRepository: RemoteUserRepository) {


    private val logger = LoggerFactory.getLogger(RegisterMessagingTokenUseCase::class.java)

    suspend operator fun invoke(token: String) {

        preferenceRepository.updateMessagingToken(token)
        remoteUserRepository.updateMessagingToken(token)
        logger.debug("Registered token $token")
    }
}