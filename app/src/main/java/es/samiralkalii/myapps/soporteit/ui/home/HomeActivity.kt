package es.samiralkalii.myapps.soporteit.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.samiralkalii.myapps.soporteit.R
import kotlinx.android.synthetic.main.toolbar.*
import org.slf4j.LoggerFactory

class HomeActivity : AppCompatActivity() {

    private val logger= LoggerFactory.getLogger(HomeActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)

        supportActionBar?.title= "Home"

    }
}
