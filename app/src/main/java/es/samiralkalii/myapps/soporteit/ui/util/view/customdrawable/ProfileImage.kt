package es.samiralkalii.myapps.soporteit.ui.util.view.customdrawable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Placeholder
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.bindImgSrc


class ProfileImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    val bgColor: Int
    val textColor: Int
    var imgUri: String
    var placeholder: Int= -1
    var text: String
    val imgView: ImageView
    val txtView: TextView

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileImage,
            0, 0)
        bgColor = ResourcesCompat.getColor(resources, a.getInt(R.styleable.ProfileImage_bgColor, R.color.colorPrimary), null)
        textColor= ResourcesCompat.getColor(resources, a.getInt(R.styleable.ProfileImage_textColor, R.color.white), null)
        imgUri= a.getString(R.styleable.ProfileImage_imgUri) ?: ""
        placeholder= a.getInt(R.styleable.ProfileImage_placeholder, R.drawable.profile)
        text= a.getString(R.styleable.ProfileImage_text) ?: ""
        a.recycle()
        View.inflate(context, R.layout.profile_image, this)
        imgView= findViewById(R.id.profile_image)
        txtView= findViewById(R.id.profile_text)
        setupUi()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius= (w/2).toFloat()
    }

    private fun setupUi() {
        if (text.isNotBlank()) {
            setupTextView(text, bgColor, textColor)
        } else {
            setupImageView(imgUri, placeholder)
        }
    }

    fun showDefaultImageProfile(textParam: String, bgColorParam: Int, textColorParam: Int) {
        if (!textParam.isNullOrBlank()) {
            text= textParam
            setupTextView(text, bgColorParam, textColorParam)
        }
    }

    private fun setupTextView(text: String, bgColor: Int, textColor: Int) {


        val cx = imgView.width/2
        val cy = imgView.height/2
        val initialRadius = 0
        txtView.setBackgroundColor(ResourcesCompat.getColor(resources, bgColor, null))
        txtView.setTextColor(ResourcesCompat.getColor(resources, textColor, null))
        txtView.text= text
        val anim = ViewAnimationUtils.createCircularReveal(imgView, cx, cy, imgView.width.toFloat()/2, initialRadius.toFloat())
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                imgView.visibility= View.GONE
                txtView.visibility= View.VISIBLE
            }
        })
        anim.start()

        //txtView.visibility= View.VISIBLE
        //imgView.visibility= View.GONE
//        txtView.setBackgroundColor(ResourcesCompat.getColor(resources, bgColor, null))
//        txtView.setTextColor(ResourcesCompat.getColor(resources, textColor, null))
//        txtView.setText(text)
    }

    private fun setupImageView(uriParam: String, placeholderParam: Int) {
        imgView.visibility= View.VISIBLE
        txtView.visibility= View.GONE
        if (uriParam.isNotBlank()) {
            Glide.with(this.context).load(uriParam).into(imgView)
        } else if (placeholder!= null) {
            imgView.setImageResource(placeholderParam)
        }
    }







}