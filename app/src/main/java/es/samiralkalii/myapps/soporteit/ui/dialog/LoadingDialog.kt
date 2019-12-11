package es.samiralkalii.myapps.soporteit.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.samiralkalii.myapps.soporteit.R



class LoadingDialog: BottomSheetDialogFragment() {

    companion object {

        const val FRAGMENT_TAG= "dialog"

        fun show(fragmentManager: FragmentManager): LoadingDialog {
            val loadingDialog= LoadingDialog()
            loadingDialog.show(fragmentManager, FRAGMENT_TAG)
            return loadingDialog
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.dialog_loading, container, false)

        return view
    }


}