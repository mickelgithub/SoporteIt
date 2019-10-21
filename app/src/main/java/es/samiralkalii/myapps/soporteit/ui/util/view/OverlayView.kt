package es.samiralkalii.myapps.soporteit.ui.util.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import es.samiralkalii.myapps.soporteit.R

class OverlayView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    init {
        setBackgroundColor(context.resources.getColor(R.color.black))
        alpha = 0.6f

        setOnTouchListener({ _, _ -> true })
    }
}