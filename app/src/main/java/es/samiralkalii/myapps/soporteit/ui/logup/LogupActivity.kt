package es.samiralkalii.myapps.soporteit.ui.logup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityLogupBinding
import es.samiralkalii.myapps.soporteit.databinding.SceneLoginFormBinding
import es.samiralkalii.myapps.soporteit.databinding.SceneLogupFormBinding
import es.samiralkalii.myapps.soporteit.ui.logup.dialog.PickUpProfilePhotoBottonSheetDialog
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.startHomeActivity
import kotlinx.android.synthetic.main.activity_logup.*
import kotlinx.android.synthetic.main.scene_logup_form.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

private val PICK_IMAGE= 1
private val PERMISSION_REQUEST_CODE= 2
private val IMAGE_MIMETYPE= "image/*"


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

        //setContentView(es.samiralkalii.myapps.soporteit.R.layout.activity_logup)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_logup)
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

        setSupportActionBar(toolbar)

        supportActionBar?.let { title= resources.getString(R.string.registration) }

        viewModel.registerState.observe(this, Observer {
            if (it is ScreenState.Render) {
                processStateLogUp(it)
            }
        })

        viewModel.loginState.observe(this, Observer {
            if (it is ScreenState.Render) {
                processStateLogin(it)
            }
        })

        viewModel.loginOrLogUp.observe(this, Observer {
            when (it) {
                LogupViewModel.TO_LOG_IN -> {
                    bindingLogin.invalidateAll()
                    nameInputLayout.visibility= View.GONE
                    TransitionManager.go(scene2, transitionMngLogUpToLogIn)
                    logger.debug(viewModel.user.toString()+ "dfsdfsdfsdf")
                    supportActionBar?.title= resources.getString(es.samiralkalii.myapps.soporteit.R.string.logIn)
                }
                LogupViewModel.TO_LOG_UP -> {
                    bindingLogup.invalidateAll()
                    TransitionManager.go(scene1, transitionMngLogUpToLogIn)
                    nameInputLayout.visibility= View.VISIBLE
                    supportActionBar?.title= resources.getString(es.samiralkalii.myapps.soporteit.R.string.registration)
                }
            }
        })
    }

    private fun processStateLogin(screenState: ScreenState.Render<LoginState>) {
        screenState.let {
            when (screenState.renderState) {
                LoginState.LoginOk -> {
                    logger.debug("Login correcto, goto Home")
                    if (viewModel.user.localProfileImage.isNotBlank()) {
                        val shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        profile_image.startAnimation(shake)
                    }
                    Handler().postDelayed(Runnable { startHomeActivity() }, 1000)
                }
                is LoginState.ShowMessage -> {
                    logger.debug("Hubo un error en acceso, lo mostramos")
                    Toast.makeText(this, resources.getString(screenState.renderState.message), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun processStateLogUp(screenState: ScreenState.Render<LogupState>) {
        screenState.let {
            when (screenState.renderState) {
                LogupState.RegisteredOk -> {
                    logger.debug("Registracion correcto, goto Home")
                    startHomeActivity()
                }
                is LogupState.ShowMessage -> {
                    logger.debug("Hubo un error en la registracion, lo mostramos")
                    Toast.makeText(this, resources.getString(screenState.renderState.message), Toast.LENGTH_LONG).show()
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
            if (checkPermission()) {
                viewModel.updateImageProfile(selectedImage)
            } else {
                requestPermission()
            }
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
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logger.debug("value", "Permission Granted, Now you can use local drive .");
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
            PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource.GALLERY -> showChooserToPickImage()
            PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource.DELETE -> Unit
        }
    }

    override fun deleteImageProfile() {
        viewModel.updateImageProfile(null)
    }

    //---------------------------------------------------------------------------------


}
