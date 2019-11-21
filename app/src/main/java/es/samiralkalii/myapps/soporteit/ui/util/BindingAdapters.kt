package es.samiralkalii.myapps.soporteit.ui.util

import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
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

@BindingAdapter("error_indication")
fun ViewGroup.bindShowError(showError: Boolean) {
    if (showError) {
        (this.background as GradientDrawable).setStroke(2, AppCompatResources.getColorStateList(context, R.color.red_error))
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

@BindingAdapter(value=arrayOf("entries", "value"), requireAll = false)
fun Spinner.bindEntries(entries: Array<String>, value: String?) {
    Log.d("TAG",".....................bindEntries...........................")
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
    Log.d("TAG", ".....................getValue...........................")
    return this.selectedItem as String
}

@BindingAdapter(value= arrayOf("valueAttrChanged"))
fun Spinner.setListeners(inverseBindingListener: InverseBindingListener?) {

    if (inverseBindingListener== null) {
        onItemClickListener= null
    } else {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (tag != position) {
                    inverseBindingListener.onChange()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}




