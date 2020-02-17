package es.samiralkalii.myapps.soporteit.ui.util

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileViewModel


@BindingAdapter("android:enabled")
fun View.bindEnabled(enabledParam: Boolean) {
    isEnabled= enabledParam
}

@BindingAdapter("visible")
fun View.bindVisible(visible: Boolean?) {
    visibility= if (visible== true) View.VISIBLE else View.GONE
}


@BindingAdapter("imgsrc")
fun ImageView.bindImgSrc(imageUri: Uri?) {
    Glide.with(this.context).load(imageUri ?: R.drawable.profile)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .into(this)
}

@BindingAdapter("imgsrc")
fun ImageView.bindImgSrcfromUrlStr(imageUri: String?) {
    Glide.with(this.context).load(if (imageUri.isNullOrBlank()) R.drawable.profile else imageUri)
        .placeholder(R.drawable.profile)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .into(this)
}

@BindingAdapter("imgsrcwithcash")
fun ImageView.bindImgSrcfromUrlStrwithCach(imageUri: String?) {
    Glide.with(this.context).load(if (imageUri.isNullOrBlank()) R.drawable.profile else imageUri)
        .placeholder(R.drawable.profile)
        .into(this)
}

@BindingAdapter("spinner_state")
fun ViewGroup.bindShowError(spinnerState: Int) {
    if (spinnerState== 1) {
        //Error indicating no selected profile value
        (this.background as GradientDrawable).setStroke(2, AppCompatResources.getColorStateList(context, R.color.red_error))
    } else if (spinnerState== 2) {
        //spinner selected
        (this.background as GradientDrawable).setStroke(5, AppCompatResources.getColorStateList(context, R.color.colorPrimary))
    } else {
        (this.background as GradientDrawable).setStroke(2, AppCompatResources.getColorStateList(context, R.color.mtrl_outlined_stroke_color))
    }
}

@BindingAdapter("error")
fun com.google.android.material.textfield.TextInputLayout.bindError(errorMessage: Int?) {
    if (errorMessage!= null) {
        error= resources.getString(errorMessage)
    } else {
        error= ""
    }
}

@BindingAdapter(value=arrayOf("option", "value"), requireAll = false)
fun Spinner.bindEntries(opcion: String, value: String?) {
    var entries: Array<String>
    if (opcion== "N"){ //Is not a Boss, we show othen profiles other than boss
        entries= resources.getStringArray(R.array.profile_members_array)
    } else { //En other cases, we show all including boss profile
        entries= resources.getStringArray(R.array.profile_array)
    }
    var itemSelectedIndex= entries.size -1
    val adapter = object: ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, entries) {
        override fun getCount(): Int {
            return (entries.size- 1)
        }
    }
    if (value!= null && value.isNotEmpty() && entries.contains(value)) {
        itemSelectedIndex= entries.indexOfFirst { it -> it== value }
    }
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter= adapter
    this.setSelection(itemSelectedIndex)
}

@InverseBindingAdapter(attribute = "value", event="valueAttrChanged")
fun Spinner.getValue(): String {
    return this.selectedItem as String
}

private fun hideSoftKeybord(context: Context) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = (context as AppCompatActivity).getCurrentFocus()
    if (view == null) {
        view = View(context)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

@BindingAdapter(value= arrayOf("valueAttrChanged", "viewmodel"), requireAll = false)
fun Spinner.setListeners(inverseBindingListener: InverseBindingListener?, viewmodel: ProfileViewModel?) {

    if (inverseBindingListener== null) {
        onItemClickListener= null
    } else {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (tag != position) {
                    inverseBindingListener.onChange()
                    if ((selectedItem as String) != context.resources.getString(R.string.choose_profile)) {
                        viewmodel?.updateShowSaveMenu()
                        hideSoftKeybord(this@setListeners.context)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}

@BindingAdapter("app:srcCompat")
fun ImageView.bindBossVerification(bossVerification: String) {
    if (bossVerification== "P" || bossVerification== "N") {
        setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.warning))
    } else if (bossVerification== "S") {
        setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ok))
    } else {
        visibility= View.GONE
    }
}

@BindingAdapter("android:text")
fun TextView.bindTextToNotificationType(notifType: NotifType) {
    if (notifType== NotifType.ACTION_INVITE_TEAM) {
        text= context.resources.getString(R.string.notif_type_invitation_to_team)
    }
}


@BindingAdapter("description")
fun TextView.bindNotificationDescrption(notification: Notification) {
    text= Html.fromHtml(context.resources.getString(R.string.notif_body_invitation_to_be_part_of_team,
        notification.senderName, notification.senderEmail, notification.team))
}

@BindingAdapter("open")
fun me.markosullivan.swiperevealactionbuttons.SwipeRevealLayout.bindOpen(open: Int) {
    when (open) {
        0 -> {
            this.close(false)
            Log.d("BINDING", "OPEN "+ 0)
        }
        1 -> {
            this.open(true)
            Log.d("BINDING", "OPEN "+ 1)
        }
        2 -> {
            this.close(true)
            Log.d("BINDING", "OPEN "+ 2)
        }
        else -> Unit
    }
}

@BindingAdapter("draglock")
fun me.markosullivan.swiperevealactionbuttons.SwipeRevealLayout.bindLockDrag(lock: Boolean) {
    dragLock(lock)
}

@BindingAdapter("app:background")
fun View.bindBackgroundColor(color: Int) {
    setBackgroundColor(ContextCompat.getColor(context, color))
}










