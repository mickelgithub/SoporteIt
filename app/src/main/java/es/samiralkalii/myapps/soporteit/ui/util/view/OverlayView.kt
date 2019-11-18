package es.samiralkalii.myapps.soporteit.ui.util.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import es.samiralkalii.myapps.soporteit.R

class OverlayView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    init {

        setBackgroundColor(ContextCompat.getColor(context, R.color.back_load))
        setOnTouchListener({ _, _ -> true })
    }
}