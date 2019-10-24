package es.samiralkalii.myapps.soporteit.ui.home

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityHomeBinding
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileFragment
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class HomeActivity : AppCompatActivity() {

    private val logger= LoggerFactory.getLogger(HomeActivity::class.java)

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel.publishUser(intent.extras?.toUser() ?: User())
        binding.viewModel= viewModel
        binding.lifecycleOwner= this
        setContentView(binding.root)
        binding.executePendingBindings()

        setSupportActionBar(toolbar)
        supportActionBar?.title= "Home"

        if (!viewModel.user.emailVerified) {
            Handler().postDelayed({
                finish()
            }, 10000)
        }

        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_item_profile -> {
                    supportActionBar?.title= resources.getString(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment()).commit()
                    true
                }
                else -> {
                    false
                }
            }



        }

    }


}
