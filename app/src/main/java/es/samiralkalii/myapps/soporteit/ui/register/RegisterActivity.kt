package es.samiralkalii.myapps.soporteit.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RegisterViewModel = ViewModelProviders.of(this)[RegisterViewModel::class.java]

        val binding: ActivityRegisterBinding= DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.viewModel= viewModel
    }
}
