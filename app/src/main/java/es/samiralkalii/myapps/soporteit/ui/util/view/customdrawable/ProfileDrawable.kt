package es.samiralkalii.myapps.soporteit.ui.util.view.customdrawable



import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint


class ProfileDrawable(val text: String, backColor: Int, txtColor: Int,
                      val padding: Float): Drawable() {

    private val textPaint: TextPaint

    init {
        textPaint= TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color= txtColor
        textPaint.typeface= Typeface.DEFAULT_BOLD
        textPaint.style= Paint.Style.FILL_AND_STROKE
    }

    fun getFitTextWidthSize(width: Float, text: String): Float {
        val nowWidth = textPaint.measureText(text)
        return ((width-padding) / nowWidth) * textPaint.textSize
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val headerFontSize = getFitTextWidthSize(bounds.width().toFloat(), text)
        textPaint.textSize= headerFontSize
        val textBound= Rect()
        textPaint.getTextBounds(text, 0, text.length, textBound)
        val offsetX= padding/2
        val offsetY= (bounds.height()/2).toFloat()+ textBound.height()/2

        canvas.drawText(text, offsetX, offsetY, textPaint)



    }

    override fun setAlpha(alpha: Int) {
        //textPaint.setAlpha(alpha);
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE;
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        //textPaint.setColorFilter(colorFilter);
    }



}
