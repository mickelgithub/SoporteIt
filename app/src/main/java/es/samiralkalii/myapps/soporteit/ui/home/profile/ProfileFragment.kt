package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentProfileBinding
import es.samiralkalii.myapps.soporteit.ui.BaseFragment
import es.samiralkalii.myapps.soporteit.ui.dialog.LoadingDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.PickUpProfilePhotoBottonSheetDialog
import es.samiralkalii.myapps.soporteit.ui.util.IMAGE_MIMETYPE
import es.samiralkalii.myapps.soporteit.ui.util.PERMISSION_REQUEST_CODE
import es.samiralkalii.myapps.soporteit.ui.util.PICK_IMAGE
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class ProfileFragment: BaseFragment(), PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener {

    private val logger= LoggerFactory.getLogger(ProfileFragment::class.java)

    override val viewModel: ProfileFragmentViewModel by viewModel()

    private lateinit var binding: FragmentProfileBinding


    override fun initUI(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        logger.debug("OnCreateView")
        binding= FragmentProfileBinding.inflate(inflater, container, false)
        binding.uiModel= viewModel.uiModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun initStateObservation() {

        viewModel.uiModel.showSaveMenu.observe(this, Observer {
            requireActivity().invalidateOptionsMenu()
        })

        viewModel.uiModel.profileChangeState.observe(this, Observer {
            it.getContentIfNotHandled().let { screenState ->
                if (screenState is ScreenState.Render) {
                    processStateProfileImageChanged(screenState)
                }
            }

        })

        viewModel.uiModel.progressVisible.observe(this, Observer {
            LoadingDialog.processDialog(it, requireActivity().supportFragmentManager)
        })
    }

    private fun processStateProfileImageChanged(screenState: ScreenState.Render<ProfileChangeState>) {
        when (screenState.renderState) {
            ProfileChangeState.changeOk -> {
                viewModel.updateProgressVisible(MyDialog.DialogState.UpdateSuccess())
            }
            is ProfileChangeState.ShowMessage -> {
                val messagedesc= if (screenState.renderState.messageParams.isNotEmpty()) resources.getString(screenState.renderState.message, *screenState.renderState.messageParams.toTypedArray()) else
                    resources.getString(screenState.renderState.message)
                viewModel.updateProgressVisible(MyDialog.DialogState.UpdateMessage(messagedesc))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_profile_fragment, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.menu_item_ok).setVisible(viewModel.uiModel.showSaveMenu.value!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_ok -> {
                //we have tu update user object
                //we have to save the file en local storage
                //we have ti save the file en remote storage
                viewModel.onSaveClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //called by binding from the xml layout
    fun onImageProfileClick() {
        //val pickUpProfilePhotoBottonSheetDialog= PickUpProfilePhotoBottonSheetDialog.newInstance(viewModel.profileImage.value!= null, ProfileFragment::class.java.simpleName)
        val pickUpProfilePhotoBottonSheetDialog= PickUpProfilePhotoBottonSheetDialog.newInstance(viewModel.uiModel.profileImage.value!= null)
        pickUpProfilePhotoBottonSheetDialog.show(requireActivity().supportFragmentManager, PickUpProfilePhotoBottonSheetDialog::class.java.simpleName)
    }

    private fun showChooserToPickImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = IMAGE_MIMETYPE

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = IMAGE_MIMETYPE

        val chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logger.debug("value", "Permission Granted, Now you can use local drive .")
                    showChooserToPickImage()
                } else {
                    logger.debug("value", "Permission Denied, You cannot use local drive .")
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                PICK_IMAGE -> pickImage(data)
            }
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(requireActivity(), getString(R.string.read_permission_indication), Toast.LENGTH_LONG).show();
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    private fun pickImage(data: Intent?) {
        //data.getData return the content URI for the selected Image
        val selectedImage = data?.data
        if (selectedImage!= null) {
            viewModel.updateImageProfile(selectedImage)
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun getProfilePhotoFrom(profilePhotoSource: PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource) {
        when (profilePhotoSource) {
            PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource.CAMERA -> logger.debug("Camera clicked.........")
            PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource.GALLERY -> {
                if (checkPermission()) {
                    showChooserToPickImage()
                } else {
                    requestPermission()
                }
            }
        }
    }

    fun onImageConfirmationClick() {
        viewModel.uiModel.user.value?.let {
            if (viewModel.uiModel.showVerified.value!! && it.isBoss) {
                viewModel.updateProgressVisible(
                    MyDialog.DialogState.ShowMessageDialog(resources.getString(
                            R.string.verified
                        ), error = false)
                )
            } else if (viewModel.uiModel.showVerified.value!! && !it.isBoss) {
                viewModel.updateProgressVisible(MyDialog.DialogState.ShowMessageDialog(resources.getString(
                        R.string.membership_confirmed
                    ), error = false)
                )
            } else if (viewModel.uiModel.showNotVerifiedYet.value!! && it.isBoss){
                viewModel.updateProgressVisible(MyDialog.DialogState.ShowMessageDialog(resources.getString(R.string.verification_pending)))
            } else if (viewModel.uiModel.showNotVerifiedYet.value!! && !it.isBoss) {
                viewModel.updateProgressVisible(MyDialog.DialogState.ShowMessageDialog(resources.getString(
                    R.string.membership_not_confirmed_yet))
                )
            }
        }
    }

    override fun deleteImageProfile() {
        viewModel.updateImageProfile(null)
    }

}