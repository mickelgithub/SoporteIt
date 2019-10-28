package es.samiralkalii.myapps.soporteit.ui.home

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityHomeBinding
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileFragment
import es.samiralkalii.myapps.soporteit.ui.util.toBundle
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

        binding= ActivityHomeBinding.inflate(layoutInflater)
        viewModel.publishUser(intent.extras?.toUser() ?: User())
        binding.viewModel= viewModel
        binding.lifecycleOwner= this
        setContentView(binding.root)
        binding.executePendingBindings()

        setSupportActionBar(toolbar)
        supportActionBar?.title= getString(R.string.app_name)

        if (!viewModel.user.emailVerified) {
            bottomNav.visibility= View.GONE
            finishMeInAwhile(1000L)
        } else {
            bottomNav.setOnNavigationItemSelectedListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.menu_item_profile -> {
                        if (supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)== null) {
                            supportActionBar?.title= resources.getString(R.string.profile)
                            supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance(viewModel.user.toBundle()), ProfileFragment::class.java.simpleName).commit()
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }

    }

    private fun finishMeInAwhile(delay: Long) {
        if (!viewModel.user.emailVerified) {
            Handler().postDelayed({
                finish()
            }, delay)
        }
    }


}
