package es.samiralkalii.myapps.soporteit.ui.util

import android.R
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


class AndroidUtility {

    companion object {

        fun hideKeyboard(activity: Activity) {
            val view: View = activity.findViewById(R.id.content)
            if (view != null) {
                val imm: InputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }
    }
}