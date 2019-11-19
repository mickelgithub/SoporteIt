package es.samiralkalii.myapps.soporteit.ui.util

import android.net.Uri
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import es.samiralkalii.myapps.soporteit.R

val FADE_IN= 1
val FADE_OUT= 2
val DEFAULT_ANIMATION_DURATION = 1000L

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

@BindingAdapter("error")
fun com.google.android.material.textfield.TextInputLayout.bindError(errorMessage: Int?) {
    if (errorMessage!= null) {
        error= resources.getString(errorMessage)
    } else {
        error= ""
    }
}

@BindingAdapter("entries")
fun Spinner.bindEntries(entries: Array<String>) {
    val adapter = object: ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, entries) {

        override fun getCount(): Int {
            return 4
        }
    }
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter= adapter
    this.setSelection(4)
}

@InverseBindingMethods({
    @InverseBindingMethod(type = android.widget.Spinner::class.java,
            attribute = "profile",
        method = "getProfile")
})


