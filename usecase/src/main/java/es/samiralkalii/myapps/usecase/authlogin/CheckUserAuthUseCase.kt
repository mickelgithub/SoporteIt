package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory

class CheckUserAuthUseCase(private val remoteUserAuthRepository: RemoteUserAuthRepository,
                           private val preferenceRepository: PreferenceRepository,
                           private val remoteUserRepository: RemoteUserRepository) {

    private val logger= LoggerFactory.getLogger(CheckUserAuthUseCase::class.java)

    sealed class Result {
        class Logged(val user: User): Result()
        class Relogged(val user: User): Result()
        object FirstAccess: Result()
        class Error(val message: String): Result()
    }

    private suspend fun updateEmailVerified(user: User) {
        preferenceRepository.updateEmailVerified()
        remoteUserRepository.updateEmailVerified(user)
    }

    suspend operator fun invoke(user: User): Result {
        if (user== User.EMPTY) {
            user.copy(preferenceRepository.getUser())
        }
        val emailVerified= user.emailVerified
        val loggedIn= remoteUserAuthRepository.checkUserLoggedIn(user)
        if (loggedIn) {
            logger.debug("active session...")
            if (!emailVerified && user.emailVerified) {
                //we have to update this informacion en preferences
                //and update it in firebase database
                updateEmailVerified(user)
            }
            return Result.Logged(user)
        } else {
            //check if the user has been logged sometime ago
            //and so he has the login info saved en sharedPreferences
            if (user != User.EMPTY) {
                 //user already registered but he has a expired token
                //we have to login
                logger.debug("expired session, login taking data from preferences...")
                remoteUserAuthRepository.signInUser(user, false)
                if (!emailVerified && user.emailVerified) {
                    //we have to update this informacion en preferences
                    //and update it in firebase database
                    updateEmailVerified(user)
                }
                return Result.Relogged(user)
            } else {
                logger.debug("First access...")
                return Result.FirstAccess
            }
        }
    }
}