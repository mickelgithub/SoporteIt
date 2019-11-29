package es.samiralkalii.myapps.soporteit.ui.home

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityHomeBinding
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileFragment
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
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

        val goto= intent.getIntExtra(SplashActivity.GOTO_KEY, -1)

        binding= ActivityHomeBinding.inflate(layoutInflater)
        viewModel.publishUserAndGoto(intent.extras?.toUser() ?: User(), goto)
        binding.viewModel= viewModel
        binding.lifecycleOwner= this
        setContentView(binding.root)
        binding.executePendingBindings()

        setSupportActionBar(toolbar)
        supportActionBar?.title= getString(R.string.app_name)

        if (!viewModel.user.emailVerified) {
            bottomNav.visibility= View.GONE
            finishMeInAwhile(5000L)
        } else {
            bottomNav.setOnNavigationItemSelectedListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.menu_item_absence -> {
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

            viewModel.goto.observe(this, Observer {
                it.getContentIfNotHandled().let { goto ->
                    if (goto!= null) {
                        gotoProfileScreen(goto)
                    }
                }}
            )
        }

    }

    private fun gotoProfileScreen(goto: SplashActivity.Companion.GOTO) {
        when (goto) {
            SplashActivity.Companion.GOTO.PROFILE -> {
                if (supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)== null) {
                    supportActionBar?.title= resources.getString(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance(viewModel.user.toBundle()), ProfileFragment::class.java.simpleName).commit()
                }
            }
            SplashActivity.Companion.GOTO.PROFILE_PROFILE_NEEDED -> {
                if (supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)== null) {
                    supportActionBar?.title= resources.getString(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance(viewModel.user.toBundle()), ProfileFragment::class.java.simpleName).commit()
                }
                showMessageDialog(R.string.profile_is_needed, R.string.advertisement)
            }
        }
    }

    private fun showMessageDialog(@StringRes message: Int, @StringRes title: Int ) {
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(getString(title))
            .setMessage(getString(message))
            .setPositiveButton(
                getString(R.string.agree)
            ) { _, _ ->
            }.setOnDismissListener { _ ->
            }
            .show()
    }

    private fun finishMeInAwhile(delay: Long) {
        if (!viewModel.user.emailVerified) {
            Handler().postDelayed({
                finish()
            }, delay)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_profile -> {
                if (supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)== null) {
                    supportActionBar?.title= resources.getString(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance(viewModel.user.toBundle()), ProfileFragment::class.java.simpleName).commit()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }
}
