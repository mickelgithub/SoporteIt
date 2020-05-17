package es.samiralkalii.myapps.soporteit.ui.logup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import androidx.core.view.doOnNextLayout
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
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.util.*
import es.samiralkalii.myapps.soporteit.ui.util.animators.animateRevealViewInverse
import kotlinx.android.synthetic.main.activity_logup.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory


class LogupActivity : BaseActivity(),
    PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener {

    private val logger = LoggerFactory.getLogger(LogupActivity::class.java)

    override val viewModel: LogupActivityViewModel by viewModel()
    private lateinit var binding: ActivityLogupBinding
    private lateinit var bindingLogup: SceneLogupFormBinding
    private val bindingLogin: SceneLoginFormBinding by lazy {
        initLoginBinding()
    }
    private lateinit var scene1: Scene
    private lateinit var scene2: Scene
    private lateinit var transitionMngLogUpToLogIn: Transition

    private fun initLoginBinding(): SceneLoginFormBinding {
        val bindingLogin= SceneLoginFormBinding.inflate(layoutInflater, container, false).apply {
            interactor= viewModel
            uiModel= viewModel.uiModel
            lifecycleOwner= this@LogupActivity
        }
        scene2= Scene(container, bindingLogin.root)
        return bindingLogin
    }

    override fun initUI() {
        hideSystemUI()
        
        binding= ActivityLogupBinding.inflate(layoutInflater).apply {
            viewModel= viewModel
            lifecycleOwner= this@LogupActivity
        }
        setContentView(binding.root)

        bindingLogup= SceneLogupFormBinding.inflate(layoutInflater, container, false).apply {
            uiModel= viewModel.uiModel
            interactor= viewModel
            lifecycleOwner= this@LogupActivity
            activity= this@LogupActivity

            cardProfileView.doOnNextLayout { v ->
                v.postDelayed({v.animateRevealViewInverse{}}, 1)
            }
        }

        scene1= Scene(container, bindingLogup.root)
        scene1.enter()
        transitionMngLogUpToLogIn= TransitionInflater.from(this).inflateTransition(R.transition.logup_login_transition)

    }

    override fun initStateObservation() {

        viewModel.uiModel.logupState.observe(this, Observer {
            it.getContentIfNotHandled()?.let { screenState ->
                if (screenState is ScreenState.Render) {
                    processStateLogUp(screenState)
                }
            }
        })

        viewModel.uiModel.loginState.observe(this, Observer {
            it.getContentIfNotHandled()?.let { screenState ->
                if (screenState is ScreenState.Render) {
                    processStateLogin(screenState)
                }
            }
        })

        viewModel.uiModel.loginOrLogUp.observe(this, Observer {
            when (it) {
                LogupActivityViewModel.ScreenLogup.LOGIN -> {
                    bindingLogin.invalidateAll()
                    TransitionManager.go(scene2, transitionMngLogUpToLogIn)
                }
                LogupActivityViewModel.ScreenLogup.LOGUP -> {
                    bindingLogup.invalidateAll()
                    TransitionManager.go(scene1, transitionMngLogUpToLogIn)
                }
            }
        })

        viewModel.uiModel.progressVisible.observe(this, Observer {
            LoadingDialog.processDialog(it, supportFragmentManager)
        })

        viewModel.uiModel.area.observe(this, Observer {
            viewModel.updateDepartmentsOfArea(it)
        })
        viewModel.uiModel.profileColor.observe(this, Observer {
            if (viewModel.uiModel.loginOrLogUp.value== LogupActivityViewModel.ScreenLogup.LOGUP) {
                bindingLogup.cardProfileView.postDelayed({
                    bindingLogup.cardProfileView.setTextView(getFirstName(viewModel.uiModel.name.value), it.first, it.second)
                }, MyDialog.DIALOG_DISMISS_DELAY+ 10)
            } else {
                bindingLogin.cardProfileView.setTextView(viewModel.uiModel.user.value!!.firstName, it.first, it.second)
            }
        })
    }

    private fun processStateLogin(screenState: ScreenState.Render<LoginState>) {
        when (screenState.renderState) {
            is LoginState.LoginOk -> {
                logger.debug("Login correcto, goto Home")
                disableInputsLogin()
                if (screenState.renderState.user.profileImage.isNotEmpty()) {
                    //val shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    //profile_image.startAnimation(shake)
                    Handler().postDelayed({viewModel.updateImageProfile(Uri.parse(screenState.renderState.user.profileImage))}, MyDialog.DIALOG_DISMISS_DELAY+ 10)
                } else {
                    Handler().postDelayed({viewModel.updateProfileColor(screenState.renderState.user.profileBackColor to screenState.renderState.user.profileTextColor)}, MyDialog.DIALOG_DISMISS_DELAY+ 10)
                }
                val confirmed= (screenState.renderState.user.isBoss && screenState.renderState.user.bossConfirmation== SI ||
                        !screenState.renderState.user.isBoss && screenState.renderState.user.membershipConfirmation== SI)
                viewModel.updateDialogState(MyDialog.DialogState.UpdateSuccess())
                Handler().postDelayed({ HomeActivity.startActivity(this, screenState.renderState.user.isEmailVerified, confirmed) }, MyDialog.DIALOG_DISMISS_DELAY*2)
            }
            is LoginState.UpdateMessage -> {
                logger.debug("Hubo un error en acceso, lo mostramos")
                val messagedesc= if (screenState.renderState.messageParams.isNotEmpty()) resources.getString(screenState.renderState.message, *screenState.renderState.messageParams.toTypedArray()) else
                    resources.getString(screenState.renderState.message)
                viewModel.updateDialogState(MyDialog.DialogState.UpdateMessage(messagedesc))
            }
        }
    }

    private fun showTeamVerificationMessage(logupState: LogupState.LoggedupAsManagerTeamOk) {
        showDialog(AlertDialog.newInstanceForMessage(getString(R.string.boss_verification_title), getString(R.string.boss_verification_msg),
            getString(R.string.agree), { HomeActivity.startActivity(this, false, false)}))
    }

    private fun processStateLogUp(screenState: ScreenState.Render<LogupState>) {
        when (screenState.renderState) {
            is LogupState.LoggedupOk -> {
                logger.debug("Registracion correcto, goto Home")
                disableInputsLogup()
                viewModel.updateDialogState(MyDialog.DialogState.UpdateSuccess())
                if (screenState.renderState.user.profileImage.isEmpty()) {
                    viewModel.updateProfileColor(Pair(screenState.renderState.user.profileBackColor, screenState.renderState.user.profileTextColor))
                }
                Handler().postDelayed({
                    HomeActivity.startActivity(this, false, false)
                }, MyDialog.DIALOG_DISMISS_DELAY*2)
            }
            is LogupState.LoggedupAsManagerTeamOk -> {
                logger.debug("Registracion correcto como jefe de equipo, mostrar mensaje y go home")
                disableInputsLogup()
                if (screenState.renderState.user.profileImage.isEmpty()) {
                    viewModel.updateProfileColor(Pair(screenState.renderState.user.profileBackColor, screenState.renderState.user.profileTextColor))
                }
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

    private fun disableInputsLogin() {
        bindingLogin.mailInputLayout.isEnabled= false
        bindingLogin.passInputLayout.isEnabled= false
        bindingLogin.loginButton.isEnabled= false
    }

    fun onImageProfileClick() {
        val pickUpProfilePhotoBottonSheetDialog= PickUpProfilePhotoBottonSheetDialog.newInstance(viewModel.uiModel.imageProfile.value!= null)
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

        val chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image))
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
                    logger.debug("value", "Permission Granted, Now you can use local drive .")
                    showChooserToPickImage()
                } else {
                    logger.debug("value", "Permission Denied, You cannot use local drive .")
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

    companion object {

        fun startLogupActivity(context: Context) {
            context.startActivity(Intent(context, LogupActivity::class.java).also {
                it.flags= Intent.FLAG_ACTIVITY_NO_ANIMATION
            })
        }
    }

}
