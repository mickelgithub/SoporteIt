package es.samiralkalii.myapps.soporteit.ui.util.view.customdrawable

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import es.samiralkalii.myapps.soporteit.R


class ProfileImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    val bgColor: Int
    val textColor: Int
    val text: String
    val imgUri: String
    val placeholder: Int
    val imgView: ImageView
    val txtView: TextView

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileImage,
            0, 0)
        bgColor = a.getInt(R.styleable.ProfileImage_bgColor, android.R.color.black)
        textColor= a.getInt(R.styleable.ProfileImage_textColor, android.R.color.white)
        text= a.getString(R.styleable.ProfileImage_text) ?: ""
        imgUri= a.getString(R.styleable.ProfileImage_imgUri) ?: ""
        placeholder= a.getInt(R.styleable.ProfileImage_placeholder, R.drawable.profile)
        a.recycle()

        View.inflate(context, R.layout.profile_image, this)
        imgView= findViewById<ImageView>(R.id.profile_image)
        txtView= findViewById<TextView>(R.id.profile_text)

        setupUi()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius= (w/2).toFloat()
    }

    private fun setupUi() {

        //setBackgroundColor(bgColor)


    }

}