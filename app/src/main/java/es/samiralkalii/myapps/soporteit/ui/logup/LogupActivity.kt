package es.samiralkalii.myapps.soporteit.ui.logup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityLogupBinding
import es.samiralkalii.myapps.soporteit.databinding.SceneLoginFormBinding
import es.samiralkalii.myapps.soporteit.databinding.SceneLogupFormBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.*
import es.samiralkalii.myapps.soporteit.ui.util.*
import kotlinx.android.synthetic.main.activity_logup.*
import kotlinx.android.synthetic.main.scene_logup_form.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory


class LogupActivity : AppCompatActivity(),
    PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener {

    private val viewModel: LogupViewModel by viewModel()
    private lateinit var binding: ActivityLogupBinding
    private lateinit var bindingLogup: SceneLogupFormBinding
    private lateinit var bindingLogin: SceneLoginFormBinding
    private lateinit var scene1: Scene
    private lateinit var scene2: Scene
    private lateinit var transitionMngLogUpToLogIn: Transition

    private val logger = LoggerFactory.getLogger(LogupActivity::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideSystemUI()

        binding= ActivityLogupBinding.inflate(layoutInflater)
        binding.viewModel= viewModel
        binding.lifecycleOwner= this
        setContentView(binding.root)

        bindingLogup= SceneLogupFormBinding.inflate(layoutInflater, container, false)
        bindingLogup.viewModel= viewModel
        bindingLogup.lifecycleOwner= this
        bindingLogup.activity= this

        scene1= Scene(container, bindingLogup.root)
        scene1.enter()

        bindingLogin= SceneLoginFormBinding.inflate(layoutInflater, container, false)
        bindingLogin.viewModel= viewModel
        bindingLogin.lifecycleOwner= this
        bindingLogin.activity= this
        scene2= Scene(container, bindingLogin.root)

        transitionMngLogUpToLogIn= TransitionInflater.from(this).inflateTransition(R.transition.logup_login_transition)


        viewModel.logupState.observe(this, Observer {
            it.getContentIfNotHandled()?.let { screenState ->
                if (screenState is ScreenState.Render) {
                    processStateLogUp(screenState)
                }
            }

        })

        viewModel.loginState.observe(this, Observer {
            it.getContentIfNotHandled()?.let { screenState ->
                if (screenState is ScreenState.Render) {
                    processStateLogin(screenState)
                }
            }
        })

        viewModel.loginOrLogUp.observe(this, Observer {
            when (it) {
                LogupViewModel.TO_LOG_IN -> {
                    bindingLogin.invalidateAll()
                    TransitionManager.go(scene2, transitionMngLogUpToLogIn)
                }
                LogupViewModel.TO_LOG_UP -> {
                    bindingLogup.invalidateAll()
                    TransitionManager.go(scene1, transitionMngLogUpToLogIn)
                }
            }
        })
        /*bindingLogup.profilSpinner.isFocusableInTouchMode= true
        bindingLogup.profilSpinner.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                viewModel.indicateSpinnerState(2)
            } else {
                viewModel.indicateSpinnerState(0)
            }
        }*/

        val dows = arrayOf(
            "Lunes",
            "Martes",
            "Miercoles",
            "Jueves",
            "Viernes",
            "Sabado",
            "Domingo"
        )
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item, dows)
        areas_dropdown.setAdapter(adapter)

        viewModel.progressVisible.observe(this, Observer {
            when (it) {
                MyDialog.DialogState.ShowLoading -> LoadingDialog.showLoading(supportFragmentManager)
                MyDialog.DialogState.ShowSuccess -> LoadingDialog.dismissMe(null)
                is MyDialog.DialogState.ShowMessage -> LoadingDialog.dismissMe(it.message)
            }
        })
    }

    private fun processStateLogin(screenState: ScreenState.Render<LoginState>) {
        screenState.let {
            when (screenState.renderState) {
                is LoginState.LoginOk -> {
                    logger.debug("Login correcto, goto Home")
                    if (viewModel.localProfileImage.isNotBlank()) {
                        val shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        profile_image.startAnimation(shake)
                    }
                    viewModel.updateProgressVisible(MyDialog.DialogState.ShowSuccess)
                    Handler().postDelayed(Runnable { startHomeActivity(screenState.renderState.user.toBundle()) }, MyDialog.DIALOG_DISMISS_DELAY)
                }
                is LoginState.ShowMessage -> {
                    logger.debug("Hubo un error en acceso, lo mostramos")
                    viewModel.updateProgressVisible(MyDialog.DialogState.ShowMessage(screenState.renderState.message))
                }
            }
        }
    }

    private fun showDialog(dialog: AlertDialog) {
        val ft = supportFragmentManager.beginTransaction()
        val prev =   supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialog.show(ft, FRAGMENT_TAG)
    }

    private fun showTeamVerificationMessage(logupState: LogupState.LoggedupAsManagerTeamOk) {
        showDialog(AlertDialog.newInstanceForMessage(getString(R.string.boss_verification_title), getString(R.string.boss_verification_msg),
            getString(R.string.agree), { startHomeActivity(logupState.user.toBundle())}))
    }

    private fun processStateLogUp(screenState: ScreenState.Render<LogupState>) {
        screenState.let {
            when (screenState.renderState) {
                is LogupState.LoggedupOk -> {
                    logger.debug("Registracion correcto, goto Home")
                    viewModel.updateProgressVisible(MyDialog.DialogState.ShowSuccess)
                    Handler().postDelayed({startHomeActivity(screenState.renderState.user.toBundle())}, MyDialog.DIALOG_DISMISS_DELAY)

                }
                is LogupState.LoggedupAsManagerTeamOk -> {
                    logger.debug("Registracion correcto como jefe de equipo, mostrar mensaje y go home")
                    viewModel.updateProgressVisible(MyDialog.DialogState.ShowSuccess)
                    Handler().postDelayed({showTeamVerificationMessage(screenState.renderState)}, MyDialog.DIALOG_DISMISS_DELAY)
                }
                is LogupState.ShowMessage -> {
                    logger.debug("Hubo un error en la registracion, lo mostramos")
                    viewModel.updateProgressVisible(MyDialog.DialogState.ShowMessage(screenState.renderState.message))
                }
            }
        }
    }
    //called by bindingLogup
    fun onImageProfileClick() {
        val pickUpProfilePhotoBottonSheetDialog= PickUpProfilePhotoBottonSheetDialog.newInstance(viewModel.imageProfile.value!= null)
        pickUpProfilePhotoBottonSheetDialog.show(supportFragmentManager, "pickUpProfilePhotoBottonSheetDialog")
    }

    private fun showChooserToPickImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = IMAGE_MIMETYPE

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = IMAGE_MIMETYPE

        val chooserIntent = Intent.createChooser(getIntent, getString(es.samiralkalii.myapps.soporteit.R.string.select_image))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE)
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                PICK_IMAGE -> pickImage(data)
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
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, getString(es.samiralkalii.myapps.soporteit.R.string.read_permission_indication), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logger.debug("value", "Permission Granted, Now you can use local drive .");
                    showChooserToPickImage()
                } else {
                    logger.debug("value", "Permission Denied, You cannot use local drive .");
                }
            }
        }
    }

    //PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener implementation!!!!

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

    override fun deleteImageProfile() {
        viewModel.updateImageProfile(null)
    }

    //---------------------------------------------------------------------------------

    override fun onResume() {
        super.onResume()

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        logger.debug("......................(${areas_input_layout.width}, ${areas_input_layout.height})")
        logger.debug("++++++++++++++++++++++(${mail_input_layout.width}, ${mail_input_layout.height})")
    }
}
