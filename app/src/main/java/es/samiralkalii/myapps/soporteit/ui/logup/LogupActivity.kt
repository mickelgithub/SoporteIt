package es.samiralkalii.myapps.soporteit.ui.logup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityLogupBinding
import es.samiralkalii.myapps.soporteit.databinding.SceneLoginFormBinding
import es.samiralkalii.myapps.soporteit.databinding.SceneLogupFormBinding
import es.samiralkalii.myapps.soporteit.ui.BaseActivity
import es.samiralkalii.myapps.soporteit.ui.dialog.*
import es.samiralkalii.myapps.soporteit.ui.util.*
import kotlinx.android.synthetic.main.activity_logup.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory


private const val DELAY_SHOW_DIALOG_ERROR= 300L

class LogupActivity : BaseActivity(),
    PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener {

    private val logger = LoggerFactory.getLogger(LogupActivity::class.java)

    private val viewModel: LogupViewModel by viewModel()
    private lateinit var binding: ActivityLogupBinding
    private lateinit var bindingLogup: SceneLogupFormBinding
    private val bindingLogin: SceneLoginFormBinding by lazy {
        initLoginBinding()
    }
    private lateinit var scene1: Scene
    private lateinit var scene2: Scene
    private lateinit var transitionMngLogUpToLogIn: Transition

    private fun initLoginBinding(): SceneLoginFormBinding {
        val bindingLogin= SceneLoginFormBinding.inflate(layoutInflater, container, false)
        bindingLogin.viewModel= viewModel
        bindingLogin.lifecycleOwner= this
        bindingLogin.activity= this
        scene2= Scene(container, bindingLogin.root)
        return bindingLogin
    }


    override fun initUI() {
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

        transitionMngLogUpToLogIn= TransitionInflater.from(this).inflateTransition(R.transition.logup_login_transition)

    }

    override fun initStateObservation() {
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

        viewModel.progressVisible.observe(this, Observer {
            LoadingDialog.processDialog(it, supportFragmentManager)
        })

        viewModel.area.observe(this, Observer {
            viewModel.updateDepartmentsOfArea(it)
        })
        viewModel.profileColor.observe(this, Observer {
            bindingLogup.cardProfileView.postDelayed({
                bindingLogup.cardProfileView.setTextView(getFirstName(viewModel.name.value), it.first, it.second)
            }, MyDialog.DIALOG_DISMISS_DELAY+ 10)

        })
    }

    private fun processStateLogin(screenState: ScreenState.Render<LoginState>) {
        screenState.let {
            when (screenState.renderState) {
                is LoginState.LoginOk -> {
                    logger.debug("Login correcto, goto Home")
                    if (viewModel.imageProfile.value.toString().isNotBlank()) {
                        val shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        //profile_image.startAnimation(shake)
                    }
                    //viewModel.updateProgressVisible(MyDialog.DialogState.ShowSuccess)
                    Handler().postDelayed(Runnable { startHomeActivity(screenState.renderState.user.toBundle()) }, MyDialog.DIALOG_DISMISS_DELAY)
                }
                is LoginState.ShowMessage -> {
                    logger.debug("Hubo un error en acceso, lo mostramos")
                    //viewModel.updateProgressVisible(MyDialog.DialogState.ShowMessage(screenState.renderState.message))
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
                    disableInputsLogup()
                    viewModel.updateDialogState(MyDialog.DialogState.UpdateSuccess())
                    Handler().postDelayed({startHomeActivity(screenState.renderState.user.toBundle())}, MyDialog.DIALOG_DISMISS_DELAY*2)
                }
                is LogupState.LoggedupAsManagerTeamOk -> {
                    logger.debug("Registracion correcto como jefe de equipo, mostrar mensaje y go home")
                    disableInputsLogup()
                    viewModel.updateDialogState(MyDialog.DialogState.UpdateSuccess())
                    Handler().postDelayed({showTeamVerificationMessage(screenState.renderState)}, MyDialog.DIALOG_DISMISS_DELAY)
                }
                is LogupState.ShowMessage -> {
                    logger.debug("Hubo un error en la registracion, lo mostramos")
                    val messagedesc= if (screenState.renderState.messageParams.isNotEmpty()) resources.getString(screenState.renderState.message, *screenState.renderState.messageParams.toTypedArray()) else
                        resources.getString(screenState.renderState.message)
                    viewModel.updateDialogState(MyDialog.DialogState.ShowMessageDialog(messagedesc))
                }
                is LogupState.UpdateMessage -> {
                    logger.debug("Hubo un error en la registracion, lo mostramos")
                    val messagedesc= if (screenState.renderState.messageParams.isNotEmpty()) resources.getString(screenState.renderState.message, *screenState.renderState.messageParams.toTypedArray()) else
                        resources.getString(screenState.renderState.message)
                    viewModel.updateDialogState(MyDialog.DialogState.UpdateMessage(messagedesc))
                }
            }
        }
    }

    private fun disableInputsLogup() {

        bindingLogup.cardProfileView.isClickable= false
        bindingLogup.nameInputLayout.isEnabled= false
        bindingLogup.mailInputLayout.isEnabled= false
        bindingLogup.passInputLayout.isEnabled= false
        bindingLogup.passConfirmationInputLayout.isEnabled= false
        bindingLogup.areasInputLayout.isEnabled= false
        bindingLogup.departmentsInputLayout.isEnabled= false
        bindingLogup.isBoss.isEnabled= false
        bindingLogup.her.isEnabled= false
        bindingLogup.logupButton.isEnabled= false
        bindingLogup.bossCategoriesInputLayout.isEnabled= false

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
                if (checkStoragePermission()) {
                    showChooserToPickImage()
                } else {
                    requestStoragePermissions()
                }
            }
        }
    }

    override fun deleteImageProfile() {
        viewModel.updateImageProfile(null)
    }

}
