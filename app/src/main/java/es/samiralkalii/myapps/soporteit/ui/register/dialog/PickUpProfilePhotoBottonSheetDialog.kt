package es.samiralkalii.myapps.soporteit.ui.register.dialog


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


private val ARG_KEY_SHOW_DELETE_OPTION= "delete"

class PickUpProfilePhotoBottonSheetDialog : BottomSheetDialogFragment() {

    private lateinit var pickProfilePhotoListener: PickProfilePhotoListener
    private lateinit var binding: FragmentPickupProfilePhotoBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate<FragmentPickupProfilePhotoBottomSheetDialogBinding>(inflater, R.layout.fragment_pickup_profile_photo_bottom_sheet_dialog, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        val view= binding.root
        val args= arguments!!
        val showDeleteOption: Boolean= args.let { args[ARG_KEY_SHOW_DELETE_OPTION] as Boolean}

        if (!showDeleteOption) {
            binding.deleteOption.visibility= View.GONE
        }

        return view
    }

    enum class ProfilePhotoSource {
        CAMERA, GALLERY, DELETE
    }

    interface PickProfilePhotoListener {
        fun getProfilePhotoFrom(profilePhotoSource: ProfilePhotoSource)
        fun deleteImageProfile()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            pickProfilePhotoListener = context as PickProfilePhotoListener
        } catch (e: ClassCastException) {
            throw IllegalStateException("${context.toString()} must implement PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener" )
        }
    }

    fun onCameraClick() {
        Handler().postDelayed({
            this@PickUpProfilePhotoBottonSheetDialog.dismiss()
            pickProfilePhotoListener.getProfilePhotoFrom(ProfilePhotoSource.CAMERA)
        }, 300L)

    }

    fun onGalleryClick() {
        Handler().postDelayed({
            this@PickUpProfilePhotoBottonSheetDialog.dismiss()
            pickProfilePhotoListener.getProfilePhotoFrom(PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource.GALLERY)
        }, 300L)

    }

    fun onDeleteClick() {
        Handler().postDelayed({
            this@PickUpProfilePhotoBottonSheetDialog.dismiss()
            pickProfilePhotoListener.deleteImageProfile()
        }, 300L)
    }


    companion object {
        fun newInstance(showDeleteOption: Boolean= false): PickUpProfilePhotoBottonSheetDialog {
            val bundle= Bundle().apply {
                putBoolean(ARG_KEY_SHOW_DELETE_OPTION, showDeleteOption)
            }
            return PickUpProfilePhotoBottonSheetDialog().apply { arguments= bundle }
        }
    }
}
