package es.samiralkalii.myapps.soporteit.ui.util.view.customdrawable

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import es.samiralkalii.myapps.soporteit.R


class ProfileImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    val bgColor: Int
    val textColor: Int

    val imgUri: String
    val placeholder: Int
    val imgView: ImageView
    //val txtView: TextView
    val textPaint: TextPaint

    var text: String= ""
        set(value) {
            if (field != value) {
                field= value
                if (value!= "") {
                    if (imgView!= null) {
                        imgView.visibility= View.GONE
                    }
                } else {
                    if (imgView!= null) {
                        imgView.visibility= View.VISIBLE
                    }
                }
                invalidate()
            }

        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileImage,
            0, 0)
        bgColor = a.getInt(R.styleable.ProfileImage_bgColor, android.R.color.black)
        textColor= a.getInt(R.styleable.ProfileImage_textColor, android.R.color.white)

        imgUri= a.getString(R.styleable.ProfileImage_imgUri) ?: ""
        placeholder= a.getInt(R.styleable.ProfileImage_placeholder, R.drawable.profile)
        text= a.getString(R.styleable.ProfileImage_text) ?: "Samir"
        a.recycle()

        View.inflate(context, R.layout.profile_image, this)
        imgView= findViewById<ImageView>(R.id.profile_image)
        //txtView= findViewById<TextView>(R.id.profile_text)

        imgView.visibility= if (text.length== 0) View.VISIBLE else View.GONE




        textPaint= TextPaint(Paint.ANTI_ALIAS_FLAG)

        setupUi()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius= (w/2).toFloat()
    }

    private fun setupUi() {

        //setBackgroundColor(bgColor)

    }



    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)

        if (text.length> 0) {
            val headerFontSize = getFitTextWidthSize(width.toFloat(), text)
            textPaint.textSize= headerFontSize
            textPaint.color= Color.parseColor("#FFFFFF")
            val textBound= Rect()
            textPaint.getTextBounds(text, 0, text.length, textBound)
            val offsetX= (width*5).toFloat()/100
            val offsetY= (height/2).toFloat()+ textBound.height()/2

            canvas?.drawColor(Color.parseColor("#000000"))
            canvas?.drawText(text, offsetX, offsetY, textPaint)

            /*val bmp=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            val miCanvas= Canvas(bmp);
            miCanvas.drawColor(colorInt)*/
        }

    }

    /*fun setText(textParam: String) {
        imgView.visibility= View.GONE
        text= textParam

    }*/

    private fun getFitTextWidthSize(width: Float, text: String): Float {
        val offset= (width*5)/100
        val nowWidth = textPaint.measureText(text)
        return ((width- offset*2) / nowWidth) * textPaint.textSize
    }
}