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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentProfileBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.LoadingDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.PickUpProfilePhotoBottonSheetDialog
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.home.isBoss
import es.samiralkalii.myapps.soporteit.ui.home.isProfilePendingToInput
import es.samiralkalii.myapps.soporteit.ui.home.isVerificationPending
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import es.samiralkalii.myapps.soporteit.ui.util.view.IMAGE_MIMETYPE
import es.samiralkalii.myapps.soporteit.ui.util.view.PERMISSION_REQUEST_CODE
import es.samiralkalii.myapps.soporteit.ui.util.view.PICK_IMAGE
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class ProfileFragment: Fragment(), PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener {

    companion object {
        fun newInstance(bundle: Bundle) = ProfileFragment().apply { arguments= bundle }
    }

    private val logger= LoggerFactory.getLogger(ProfileFragment::class.java)

    private val viewModel: ProfileViewModel by viewModel()
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    private lateinit var user: User

    private lateinit var binding: FragmentProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("OnCreate")
        user= (arguments as Bundle).toUser()
        viewModel.publishUser(user)
        setHasOptionsMenu(true)

        viewModel.showSaveMenu.observe(this, Observer {
            activity!!.invalidateOptionsMenu()
        })

        viewModel.profileChangeState.observe(this, Observer {
            it.getContentIfNotHandled().let { screenState ->
                if (screenState is ScreenState.Render) {
                    processStateProfileImageChanged(screenState)
                }
            }

        })

        viewModel.profileChanged.observe(this, Observer {
            it.getContentIfNotHandled().let { update ->
                if (update!= null && update) {
                    binding.invalidateAll()
                    homeViewModel.updateProfile(user)
                }
            }
        })

        viewModel.progressVisible.observe(this, Observer {
            when (it) {
                LoadingDialog.DialogState.ShowLoading -> LoadingDialog.showMe(activity!!.supportFragmentManager)
                LoadingDialog.DialogState.ShowSuccess -> LoadingDialog.dismissMe(null)
                is LoadingDialog.DialogState.ShowMesage -> LoadingDialog.dismissMe(it.message)
            }
        })

    }

    private fun processStateProfileImageChanged(screenState: ScreenState.Render<ProfileChangeState>) {
        when (screenState.renderState) {
            ProfileChangeState.changeOk -> {
                homeViewModel.updateProfileImage(user)
                homeViewModel.updateBossVerification(user.bossVerification)
                viewModel.updateProgressVisible(LoadingDialog.DialogState.ShowSuccess)
            }
            is ProfileChangeState.ShowMessage -> {
                viewModel.updateProgressVisible(LoadingDialog.DialogState.ShowMesage(screenState.renderState.message))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile_fragment, menu)
        menu.findItem(R.id.menu_item_profile)?.setVisible(false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.menu_item_ok).setVisible(viewModel.showSaveMenu.value!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_ok -> {
                //we have tu update user object
                //we have to save the file en local storage
                //we have ti save the file en remote storage
                viewModel.onSaveClick(resources.getString(R.string.choose_profile))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logger.debug("OnCreateView")
        binding= FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel= viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("OnViewCreated")

    }

    //called by binding from the xml layout
    fun onImageProfileClick() {
        val pickUpProfilePhotoBottonSheetDialog= PickUpProfilePhotoBottonSheetDialog.newInstance(viewModel.imageProfile.value!= null, ProfileFragment::class.java.simpleName)
        pickUpProfilePhotoBottonSheetDialog.show(activity!!.supportFragmentManager, PickUpProfilePhotoBottonSheetDialog::class.java.simpleName)
    }

    private fun showChooserToPickImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = IMAGE_MIMETYPE

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = IMAGE_MIMETYPE

        val chooserIntent = Intent.createChooser(getIntent, getString(es.samiralkalii.myapps.soporteit.R.string.select_image))
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
        // Result code is RESULT_OK only if the user selects an Image

        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                PICK_IMAGE -> pickImage(data)
            }
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(activity!!, getString(es.samiralkalii.myapps.soporteit.R.string.read_permission_indication), Toast.LENGTH_LONG).show();
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
        val result = ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)
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

    fun onImageBossVerificationClick() {
        if (viewModel.user.isBoss()) {
            LoadingDialog.showMeForAwhile(activity!!.supportFragmentManager, R.string.verified)
        } else if (viewModel.user.isVerificationPending()) {
            LoadingDialog.showMeForAwhile(activity!!.supportFragmentManager, R.string.verification_pending)
        } else if (viewModel.user.isProfilePendingToInput(activity!!)) {
            LoadingDialog.showMeForAwhile(activity!!.supportFragmentManager, R.string.profile_is_needed)
        }
    }

    override fun deleteImageProfile() {
        viewModel.updateImageProfile(null)
    }

    override fun onStart() {
        super.onStart()
        logger.debug("OnStart")
    }

    override fun onResume() {
        super.onResume()
        logger.debug("OnResume")
    }

    override fun onPause() {
        super.onPause()
        logger.debug("OnPause")
    }

    override fun onStop() {
        super.onStop()
        logger.debug("OnStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("OnDestroy")
    }
}