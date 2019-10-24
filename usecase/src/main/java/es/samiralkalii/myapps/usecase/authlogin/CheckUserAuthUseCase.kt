package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.slf4j.LoggerFactory


class CheckUserAuthUseCase(private val userAccessRepository: UserAccessRepository,
                           private val preferenceRepository: PreferenceRepository,
                           private val userDatabaseRepository: UserDatabaseRepository) {

    private val logger= LoggerFactory.getLogger(CheckUserAuthUseCase::class.java)

    sealed class Result {
        class Logged(val user: User): Result()
        class Relogged(val user: User): Result()
        object FirstAccess: Result()
        class Error(val message: String): Result()
    }

    private suspend fun updateEmailVerified(user: User) {
        preferenceRepository.updateEmailVerified()
        userDatabaseRepository.updateEmailVerified(user)

    }

    suspend fun checkUserAuth(): Result {
        val user = preferenceRepository.getUserFromPreferences()
        val emailVerified= user.emailVerified
        val loggedIn= userAccessRepository.checkUserLoggedIn(user)
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
            if (user != User.Empty) {
                 //user already registered but he has a expired token
                //we have to login
                logger.debug("expired session, login taking data from preferences...")
                userAccessRepository.signInUser(user, false)
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