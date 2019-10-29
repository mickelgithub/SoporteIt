package es.samiralkalii.myapps.soporteit.framework.localstorage.db.manager

import androidx.lifecycle.LiveData
import es.samiralkalii.myapps.database.IUserDatabase
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.dao.UserDao

class UserHandler(val userDao: UserDao): IUserDatabase {


    override suspend fun addOrUpdateUser(user: User) {
        userDao.insert(user.mapToDbUser())
    }

    override suspend fun getUser(): LiveData<User> {
        userDao.getUser()
    }
}

fun User.mapToDbUser()= es.samiralkalii.myapps.soporteit.framework.localstorage.db.model.User(
    this.email, this.password, this.name, this.id, this.localProfileImage,
    this.remoteProfileImage, this.creationDate, this.emailVerified)