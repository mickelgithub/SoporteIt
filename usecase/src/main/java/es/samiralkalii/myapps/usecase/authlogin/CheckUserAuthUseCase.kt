package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory


class CheckUserAuthUseCase(private val userAccessRepository: UserAccessRepository,
                           private val preferenceRepository: PreferenceRepository) {

    private val logger= LoggerFactory.getLogger(CheckUserAuthUseCase::class.java)

    sealed class Result {
        object Logged: Result()
        object Relogged: Result()
        object FirstAccess: Result()
        class Error(val message: String): Result()
    }


    suspend fun checkUserAuth(): Result {
        val loggedIn= userAccessRepository.checkUserLoggedIn()
        if (loggedIn) {
            logger.debug("active session...")
            return Result.Logged
        } else {
            //check if the user has been logged sometime ago
            //and so he has the login info saved en sharedPreferences
            val user = preferenceRepository.getUserFromPreferences()
            if (user != User.Empty) {
                 //user already registered but he has a expired token
                //we have to login
                logger.debug("expired session, login taking data from preferences...")
                userAccessRepository.signInUser(user, false)
                return Result.Relogged
            } else {
                logger.debug("First access...")
                return Result.FirstAccess
            }
        }
    }
}