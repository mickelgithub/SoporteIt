package es.samiralkalii.myapps.soporteit.framework.localstorage.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.dao.UserDao
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.model.User

const val DATABASE_NAME= "mydatabase.db"

@Database(entities = [User::class], version= 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

}