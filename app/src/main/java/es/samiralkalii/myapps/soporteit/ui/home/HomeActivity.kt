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
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityHomeBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.FRAGMENT_TAG
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileFragment
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import es.samiralkalii.myapps.soporteit.ui.util.toBundle
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import es.samiralkalii.myapps.soporteit.ui.util.view.toast
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
            //bottomNav.menu.getItem(0).isCheckable= false
            bottomNav.setOnNavigationItemSelectedListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.menu_item_absence -> {
                        toast("Pronto tendremos esta funcionalidad de ausencias")
                        true
                    }
                    R.id.menu_item_holidays -> {
                        toast("Pronto tendremos esta funcionalidad de vacaciones")
                        true
                    }
                    R.id.menu_item_notifications -> {
                        toast("Pronto tendremos esta funcionalidad de notificaciones")
                        true
                    }
                    R.id.menu_item_planning -> {
                        toast("Pronto tendremos esta funcionalidad de crear planificaciones")
                        true
                    }
                    R.id.menu_item_home -> {
                        toast("Pronto tendremos esta funcionalidad de INICIO")
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
                        gotoScreen(goto)
                    }
                }}
            )
        }

    }

    private fun gotoScreen(goto: SplashActivity.Companion.GOTO) {
        bottomNav.menu.getItem(0).isCheckable= true
        when (goto) {
            SplashActivity.Companion.GOTO.PROFILE, SplashActivity.Companion.GOTO.PROFILE_PROFILE_NEEDED -> {
                logger.debug("Mostramos el perfil...")
                if (supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)== null) {
                    supportActionBar?.title= resources.getString(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance(viewModel.user.toBundle()), ProfileFragment::class.java.simpleName).commit()
                }
                if (goto== SplashActivity.Companion.GOTO.PROFILE_PROFILE_NEEDED) {
                    showMessageDialog(R.string.profile_is_needed, R.string.advertisement)
                }
            }
            SplashActivity.Companion.GOTO.HOME -> {
                logger.debug("Mostramos el home...")
                if (supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)== null) {
                    if (!viewModel.user.teamCreated && viewModel.user.isBoss()) {
                        supportActionBar?.title= resources.getString(R.string.team_no_created_title)
                    } else {
                        supportActionBar?.title= resources.getString(R.string.team_created_title, viewModel.user.team)
                    }

                    supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment.newInstance(viewModel.user.toBundle()), HomeFragment::class.java.simpleName).commit()
                }

            }
            /*SplashActivity.Companion.GOTO.TEAM_MANAGEMENT -> {
                //bottomNav.menu.getItem(0).isCheckable= false
                if (supportFragmentManager.findFragmentByTag(TeamMangementFragment::class.java.simpleName)== null) {
                    supportActionBar?.title= resources.getString(R.string.team_management)
                    supportFragmentManager.beginTransaction().replace(R.id.container, TeamMangementFragment.newInstance(viewModel.user.toBundle()), TeamMangementFragment::class.java.simpleName).commit()
                }
            }*/
        }
    }

    private fun showMessageDialog(@StringRes message: Int, @StringRes title: Int ) {
        val alertDialog= AlertDialog.newInstanceForMessage(getString(title), getString(message), getString(R.string.agree), { })
        val ft = supportFragmentManager.beginTransaction()
        val prev =   supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        alertDialog.show(ft, FRAGMENT_TAG)
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
        /*if (!viewModel.user.isBoss()) {
            menu.findItem(R.id.menu_item_create_team).setVisible(false)
        }*/
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_profile -> {
                if (viewModel.user.isProfilePending()) {
                    viewModel.updateGoto(SplashActivity.Companion.GOTO.PROFILE_PROFILE_NEEDED)
                } else {
                    viewModel.updateGoto(SplashActivity.Companion.GOTO.PROFILE)
                }
                true
            }
            R.id.menu_item_create_team -> {
                //viewModel.updateGoto(SplashActivity.Companion.GOTO.TEAM_MANAGEMENT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}
