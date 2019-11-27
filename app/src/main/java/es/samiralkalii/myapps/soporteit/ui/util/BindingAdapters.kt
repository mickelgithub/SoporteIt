package es.samiralkalii.myapps.soporteit.ui.util

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileViewModel

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





