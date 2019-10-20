package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.domain.User
import org.slf4j.LoggerFactory

class LoginUserCase(private val userAccessRepository: UserAccessRepository) {

    private val logger = LoggerFactory.getLogger(LoginUserCase::class.java!!)

    sealed class Result() {
        class LoginOk(): Result()
    }

    suspend fun loginUser(user: User): Result {
        logger.debug("Vamos a login el usuario ${user.email}")
        val uid = userAccessRepository.signInUser(user)
        //registration OK
        //we have to add the user profile image de local and remote storage

        return Result.LoginOk()
    }


}