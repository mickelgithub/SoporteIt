package es.samiralkalii.myapps.data.authlogin

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType
import es.samiralkalii.myapps.domain.User

class RemoteUserAuthRepository(val remoteUserAuthDataSource: IRemoteUserAuthDataSource) {

    suspend fun logupUser(user: String, pass: String)= remoteUserAuthDataSource.logupUser(user, pass)
    suspend fun signInUser(user: String, pass: String, firstTime: Boolean)= remoteUserAuthDataSource.signInUser(user, pass, firstTime)
    suspend fun sendEmailVerification(user: User)= remoteUserAuthDataSource.sendEmailVerification(user)
    suspend fun checkUserLoggedIn(isEmailAlreadyVerified: Boolean)= remoteUserAuthDataSource.checkUserLoggedIn(isEmailAlreadyVerified)

}

interface IRemoteUserAuthDataSource {
    suspend fun logupUser(user: String, pass: String): Pair<String, String>
    //return if the user has emailVerified when login
    suspend fun signInUser(user: String, pass: String, firstTime: Boolean): Pair<Boolean, String>
    //this method return (logedIn, emailVerified) pair
    suspend fun checkUserLoggedIn(isEmailAlreadyVerified: Boolean): Pair<Boolean, Boolean>
    suspend fun sendEmailVerification(user: User)
}