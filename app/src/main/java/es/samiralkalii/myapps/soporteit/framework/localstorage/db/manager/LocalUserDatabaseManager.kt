package es.samiralkalii.myapps.soporteit.framework.localstorage.db.manager

import es.samiralkalii.myapps.database.ILocalUserDatabase
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.dao.UserDao
import org.slf4j.LoggerFactory

class LocalUserDatabaseManager(val userDao: UserDao): ILocalUserDatabase {

    private val logger = LoggerFactory.getLogger(LocalUserDatabaseManager::class.java)

    /*override suspend fun addOrUpdateUser(user: User) {
        logger.debug("inserting user ${user.name}")
        userDao.insert(user.mapToDbUser())
        logger.debug("ok inserting user ${user.name}")

    }*/


    //override suspend fun getUser(): LiveData<User> = Transformations.map(userDao.getUser(), ::toUserDomain)

    private fun toUserDomain(user: es.samiralkalii.myapps.soporteit.framework.localstorage.db.model.User)= user.mapToUserDomain()





}

fun User.mapToDbUser()= es.samiralkalii.myapps.soporteit.framework.localstorage.db.model.User(
    this.email, this.password, this.name, this.id, this.localProfileImage,
    this.remoteProfileImage, this.creationDate, this.emailVerified)

fun es.samiralkalii.myapps.soporteit.framework.localstorage.db.model.User.mapToUserDomain()= User(
    email, password, name, id, localProfileImage ?: "",
    remoteProfileImage ?: "", creationDate ?: 0L,
    emailVerified ?: false)