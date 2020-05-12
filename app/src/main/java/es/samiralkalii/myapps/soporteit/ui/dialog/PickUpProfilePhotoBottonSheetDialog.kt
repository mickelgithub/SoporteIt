package es.samiralkalii.myapps.soporteit.ui.dialog


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentPickupProfilePhotoBottomSheetDialogBinding


private const val KEY_ARG_SHOW_DELETE_OPTION= "delete"
private const val KEY_FRAGMENT_TAG= "fragment"

class PickUpProfilePhotoBottonSheetDialog : BottomSheetDialogFragment() {

    //private lateinit var pickProfilePhotoListener: PickProfilePhotoListener
    private lateinit var binding: FragmentPickupProfilePhotoBottomSheetDialogBinding

    private var showDeleteOption: Boolean= false
    private var fragmentTag: String= ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val args= arguments
        args?.let {
            showDeleteOption = args[KEY_ARG_SHOW_DELETE_OPTION] as Boolean
            fragmentTag = args[KEY_FRAGMENT_TAG] as String
        }
        try {
            if (context is PickProfilePhotoListener) {
                //pickProfilePhotoListener = context
            } else {

                //pickProfilePhotoListener= (context as AppCompatActivity).supportFragmentManager.findFragmentByTag(fragmentTag) as PickProfilePhotoListener
            }

        } catch (e: ClassCastException) {
            throw IllegalStateException("${context.toString()} must implement PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener" )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate<FragmentPickupProfilePhotoBottomSheetDialogBinding>(inflater, R.layout.fragment_pickup_profile_photo_bottom_sheet_dialog, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        val view= binding.root

        if (!showDeleteOption) {
            binding.deleteOption.visibility= View.GONE
        }

        return view
    }

    enum class ProfilePhotoSource {
        CAMERA, GALLERY
    }

    interface PickProfilePhotoListener {
        fun getProfilePhotoFrom(profilePhotoSource: ProfilePhotoSource)
        fun deleteImageProfile()
    }



    fun onCameraClick() {
        Handler().postDelayed({
            this@PickUpProfilePhotoBottonSheetDialog.dismiss()
            //pickProfilePhotoListener.getProfilePhotoFrom(ProfilePhotoSource.CAMERA)
        }, 300L)

    }

    fun onGalleryClick() {
        Handler().postDelayed({
            this@PickUpProfilePhotoBottonSheetDialog.dismiss()
            //pickProfilePhotoListener.getProfilePhotoFrom(ProfilePhotoSource.GALLERY)
        }, 300L)

    }

    fun onDeleteClick() {
        Handler().postDelayed({
            this@PickUpProfilePhotoBottonSheetDialog.dismiss()
            //pickProfilePhotoListener.deleteImageProfile()
        }, 300L)
    }


    companion object {
        fun newInstance(showDeleteOption: Boolean= false, fragmentTag: String= ""): PickUpProfilePhotoBottonSheetDialog {
            val bundle= Bundle().apply {
                putBoolean(KEY_ARG_SHOW_DELETE_OPTION, showDeleteOption)
                putString(KEY_FRAGMENT_TAG, fragmentTag)
            }
            return PickUpProfilePhotoBottonSheetDialog()
                .apply { arguments= bundle }
        }
    }
}
