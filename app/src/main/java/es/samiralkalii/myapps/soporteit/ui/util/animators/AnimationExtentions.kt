package es.samiralkalii.myapps.soporteit.ui.util.animators

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils

fun View.animateRevealView(onFinishAnim: () -> Unit) {
    val cx = width/2
    val cy = height/2
    val initialRadius = width/2
    val endRadius= 0

    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius.toFloat(), endRadius.toFloat())
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            onFinishAnim()
        }
    })
    anim.start()
}

fun View.animateRevealViewInverse(onFinishAnim: () -> Unit) {

    if (this.isAttachedToWindow) {
        val cx = width/2
        val cy = height/2
        val initialRadius = 0
        val endRadius= width/2

        val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius.toFloat(), endRadius.toFloat())
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                onFinishAnim()
            }
        })
        anim.start()
    }

}