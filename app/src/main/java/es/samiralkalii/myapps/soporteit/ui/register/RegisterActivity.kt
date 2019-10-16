package es.samiralkalii.myapps.soporteit.ui.register

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.databinding.ActivityRegisterBinding
import es.samiralkalii.myapps.soporteit.ui.register.dialog.PickUpProfilePhotoBottonSheetDialog
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.startHomeActivity
import org.koin.android.viewmodel.ext.android.viewModel

private val TAG= "RegisterActivity"
private val PICK_IMAGE= 1
private val PERMISSION_REQUEST_CODE= 2


class RegisterActivity : AppCompatActivity(),
    PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener {

    private val viewModel: RegisterViewModel by viewModel()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this, es.samiralkalii.myapps.soporteit.R.layout.activity_register)
        binding.viewModel= viewModel
        binding.lifecycleOwner= this
        binding.activity= this

        supportActionBar?.let { title= resources.getString(es.samiralkalii.myapps.soporteit.R.string.registration) }

        viewModel.registerState.observe(this, Observer {
            if (it is ScreenState.Render) {
                processState(it)
            }
        })



    }

    private fun processState(screenState: ScreenState.Render<RegisterState>) {
        screenState.let {
            when (screenState.renderState) {
                RegisterState.RegisteredOk -> {
                    Log.d(TAG, "Registered OK, goto home")
                    startHomeActivity()
                }
                is RegisterState.ShowMessage -> {
                    Toast.makeText(this, screenState.renderState.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    //called by binding
    fun onImageProfileClick() {
        val pickUpProfilePhotoBottonSheetDialog= PickUpProfilePhotoBottonSheetDialog.newInstance(viewModel.imageProfile.value!= null)
        pickUpProfilePhotoBottonSheetDialog.show(supportFragmentManager, "pickUpProfilePhotoBottonSheetDialog")
    }



    private fun showChooserToPickImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Seleccionar una imagen")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE)
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result code is RESULT_OK only if the user selects an Image

        if (resultCode === Activity.RESULT_OK)
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
            Toast.makeText(this, "El permiso para Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
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
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
            }
        }
    }

    //PickUpProfilePhotoBottonSheetDialog.PickProfilePhotoListener implementation!!!!

    override fun getProfilePhotoFrom(profilePhotoSource: PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource) {
        when (profilePhotoSource) {
            PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource.CAMERA -> Log.d(TAG, "Camera clicked.........")
            PickUpProfilePhotoBottonSheetDialog.ProfilePhotoSource.GALLERY -> showChooserToPickImage()
        }
    }

    override fun deleteImageProfile() {
        viewModel.updateImageProfile(null)
    }

    //---------------------------------------------------------------------------------
}
