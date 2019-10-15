package es.samiralkalii.myapps.soporteit.ui.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import es.samiralkalii.myapps.soporteit.R

@BindingAdapter("visible")
fun View.bindVisible(visible: Boolean?) {
    visibility= if (visible== true) View.VISIBLE else View.GONE
}


@BindingAdapter("imgsrc")
fun ImageView.bindImgSrc(imgsrc: String?) {
    Glide.with(this.context).load(imgsrc ?: R.drawable.profile).into(this)
}