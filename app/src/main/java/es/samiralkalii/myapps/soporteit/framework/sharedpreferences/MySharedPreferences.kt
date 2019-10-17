package es.samiralkalii.myapps.soporteit.framework.sharedpreferences

import android.content.Context
import android.text.TextUtils
import androidx.core.content.edit
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.preference.IPreferences
import es.samiralkalii.myapps.soporteit.R

private val USER_NAME_KEY= "username_key"
private val USER_MAIL_KEY= "usermail_key"
private val USER_PASS_KEY= "userpass_key"
private val IMAGE_USER_PROFILE_URL= "profile_image_url_key"
private val IMAGE_USER_PROFILE_LOCAL= "profile_image_path_key"



class MySharedPreferences(val context: Context): IPreferences {

    override suspend fun saveUser(user: User) {
        context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).edit {
            putString(USER_NAME_KEY, user.name);
            putString(USER_MAIL_KEY, user.email)
            putString(USER_PASS_KEY, user.password)
            putString(IMAGE_USER_PROFILE_LOCAL, user.localProfileImage)
            putString(IMAGE_USER_PROFILE_URL, user.remoteProfileImage)
        }
    }

    override suspend fun getUserFromPreferences(): User= context.getSharedPreferences(context.resources.getString(R.string.preference_file), Context.MODE_PRIVATE).run {
        val name= getString(USER_NAME_KEY, "") ?: ""
        val email= getString(USER_MAIL_KEY, "") ?: ""
        val pass= getString(USER_PASS_KEY, "") ?: ""
        val imageProfilePath= getString(IMAGE_USER_PROFILE_LOCAL, "") ?: ""
        val imageProfileUrl= getString(IMAGE_USER_PROFILE_URL, "") ?: ""
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            return User.Empty
        }
        User(email, pass, name, localProfileImage = imageProfilePath, remoteProfileImage = imageProfileUrl)
    }

}