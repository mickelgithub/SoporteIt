package es.samiralkalii.myapps.usecase.authlogin

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.preference.PreferenceRepository


class CheckUserAuthUseCase(private val userAccessRepository: UserAccessRepository,
                           private val preferenceRepository: PreferenceRepository) {

    sealed class Result {
        object Logged: Result()
        object Relogged: Result()
        object FirstAccess: Result()
        class Error(val message: String): Result()
    }


    suspend fun checkUserAuth(): Result {
        val logged = false//userAccessRepository.checkUserLoggedIn()
        if (logged) {
            return Result.Logged
        } else {
            //check if the user has been logged sometime ago
            //and so he has the login info saved en sharedPreferences
            val user = preferenceRepository.getUserFromPreferences()
            //TODO

            //if (user != User.Empty) {
            if (false) {
                //user already registered but he has a expired token
                //we have to login
                userAccessRepository.signInUser(user)
                return Result.Relogged
            } else {
                return Result.FirstAccess
            }
        }
    }
}