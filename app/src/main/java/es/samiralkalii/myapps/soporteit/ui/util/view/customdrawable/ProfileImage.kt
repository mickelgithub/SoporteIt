package es.samiralkalii.myapps.soporteit.ui.util.view.customdrawable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import es.samiralkalii.myapps.soporteit.R
import org.slf4j.LoggerFactory


class ProfileImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val logger = LoggerFactory.getLogger(ProfileImage::class.java)

    var bgColor: Int
    var textColor: Int
    var imgUri: String
    var placeholder: Drawable?
    var text: String
    val imgView: ImageView
    val txtView: TextView

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileImage,
            0, 0)
        bgColor = ResourcesCompat.getColor(resources, a.getInt(R.styleable.ProfileImage_bgColor, R.color.colorPrimary), null)
        textColor= ResourcesCompat.getColor(resources, a.getInt(R.styleable.ProfileImage_textColor, R.color.white), null)
        imgUri= a.getString(R.styleable.ProfileImage_imgUri) ?: ""
        placeholder= a.getDrawable(R.styleable.ProfileImage_placeholder)
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
            setTextView(text, bgColor, textColor)
        } else {
            setImageView(imgUri)
        }
    }

    fun showDefaultImageProfile(textParam: String, bgColorParam: Int, textColorParam: Int) {
        if (!textParam.isNullOrBlank()) {
            text= textParam
            setTextView(text, bgColorParam, textColorParam)
        }
    }

    fun setTextView(textParam: String?, bgColorParam: Int, textColorParam: Int) {
        text= textParam ?: ""
        if (!textParam.isNullOrBlank()) {
            txtView.text= textParam
            bgColor= bgColorParam
            textColor= textColorParam
            txtView.setBackgroundColor(bgColor)
            txtView.setTextColor(textColor)
            if (imgView.isVisible) {
                animateRevealView(imgView) {
                    imgView.isVisible= false
                    txtView.isVisible= true
                    fadeIn(txtView, {})
                }
            } else {
                txtView.isVisible= true
                fadeIn(txtView, {})
            }
        }
    }

    fun setTextView(textParam: String?) {
        text= textParam ?: ""
        if (imgUri.isNullOrBlank()) {
            if (!textParam.isNullOrBlank()) {
                txtView.text= textParam
                txtView.setBackgroundColor(bgColor)
                txtView.setTextColor(textColor)
                if (imgView.isVisible && imgView.isAttachedToWindow) {
                    animateRevealView(imgView) {
                        imgView.isVisible= false
                        txtView.isVisible= true
                        fadeIn(txtView, {})
                    }
                } else {
                    txtView.isVisible= true
                    fadeIn(txtView, {})
                }
            }
        }
    }

    fun setImageView(uriParam: String?) {
        if (!uriParam.isNullOrBlank()) {
            if (!imgUri.isNullOrBlank() && imgUri!= uriParam) {
                fadeOut(imgView) {
                    imgView.alpha= 1F
                    Glide.with(this.context).load(uriParam)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(imgView)
                    animateRevealViewInverse(imgView, {})
                }
            } else if (imgUri.isNullOrBlank()) {
                if (!text.isNullOrBlank() && txtView.isVisible && txtView.isAttachedToWindow) {
                    fadeOut(txtView) {
                        txtView.isVisible= false
                        imgView.isVisible= true
                        Glide.with(this.context).load(uriParam)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(imgView)
                        animateRevealView(imgView, {})
                    }
                } else {
                    fadeOut(imgView) {
                        imgView.alpha= 1F
                        Glide.with(this.context).load(uriParam)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(imgView)
                        animateRevealViewInverse(imgView, {})
                    }
                }
            }
        } else if (!text.isNullOrBlank()) {
            setTextView(text)
        } else if (placeholder!= null) {
            fadeOut(imgView) {
                imgView.alpha= 1F
                Glide.with(this.context).load(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(imgView)
                animateRevealViewInverse(imgView, {})
            }
        }
        /*if (showImage && txtView.isVisible && txtView.isAttachedToWindow) {
            animateRevealView(txtView) {
                txtView.isVisible= false
                imgView.isVisible= true
                fadeIn(imgView, {logger.debug("Ya hemos terminado el fade in")})
            }
        } else if (showImage && uriParam!= null && uriParam.isNotBlank() && !imgUri.isNullOrBlank() && uriParam!= imgUri) {
            animateRevealView(imgView) {
                //txtView.isVisible= false
                //imgView.isVisible= true
                //fadeIn(imgView, {logger.debug("Ya hemos terminado el fade in")})
            }
        }*/
        imgUri= uriParam ?: ""
    }

    private fun animateRevealView(view: View, onFinishAnim: () -> Unit) {
        val cx = view.width/2
        val cy = view.height/2
        val initialRadius = view.width/2
        val endRadius= 0

        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius.toFloat(), endRadius.toFloat())
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                onFinishAnim()
            }
        })
        anim.start()

    }

    private fun animateRevealViewInverse(view: View, onFinishAnim: () -> Unit) {
        val cx = view.width/2
        val cy = view.height/2
        val initialRadius = 0
        val endRadius= view.width/2

        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius.toFloat(), endRadius.toFloat())
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                onFinishAnim()
            }
        })
        anim.start()

    }

    private fun fadeIn(view: View, onFinishAnim: () -> Unit) {
        view.alpha= 0F
        val anim= view.animate().setDuration(1500).alpha(1F)
        anim.setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                onFinishAnim()
            }
        })
    }

    private fun fadeOut(view: View, onFinishAnim: () -> Unit) {
        view.alpha= 1F
        val anim= view.animate().setDuration(300).alpha(0F)
        anim.setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                onFinishAnim()
            }
        })
    }

}