package es.samiralkalii.myapps.soporteit.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.samiralkalii.myapps.soporteit.R
import kotlinx.android.synthetic.main.toolbar.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)



        supportActionBar?.title= "Home"
        /*supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setLogo(R.drawable.camera);
        supportActionBar?.setDisplayUseLogoEnabled(true);*/
    }
}
