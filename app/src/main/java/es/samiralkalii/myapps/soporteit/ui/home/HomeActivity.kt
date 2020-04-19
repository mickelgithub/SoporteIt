package es.samiralkalii.myapps.soporteit.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityHomeBinding
import es.samiralkalii.myapps.soporteit.ui.BaseActivity
import es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.FRAGMENT_TAG
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragment
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileFragment
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import es.samiralkalii.myapps.soporteit.ui.util.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

class HomeActivity : BaseActivity() {


    private val logger= LoggerFactory.getLogger(HomeActivity::class.java)

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: ActivityHomeBinding
    private var isEmailVerified: Boolean by Delegates.notNull<Boolean>()


    override fun initUI() {

        val goto= intent.getIntExtra(GOTO_KEY, -1)
        isEmailVerified= intent.getBooleanExtra(IS_EMAIL_VERIFIED_KEY, false)

        binding= ActivityHomeBinding.inflate(layoutInflater)
        viewModel.init(goto, isEmailVerified)
        binding.viewModel= viewModel
        binding.lifecycleOwner= this
        setContentView(binding.root)
        binding.executePendingBindings()

        setSupportActionBar(toolbar)
        supportActionBar?.title= getString(R.string.app_title_need_mail_validation)

        if (!isEmailVerified) {
            finishMeInAwhile(7000L)
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
                        viewModel.updateGoto(SplashActivity.Companion.GOTO.NOTIFICATIONS)
                        true
                    }
                    R.id.menu_item_planning -> {
                        toast("Pronto tendremos esta funcionalidad de crear planificaciones")
                        true
                    }
                    R.id.menu_item_home -> {
                        viewModel.updateGoto(SplashActivity.Companion.GOTO.HOME)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    override fun initStateObservation() {
        viewModel.goto.observe(this, Observer {
            it.getContentIfNotHandled().let { goto ->
                if (goto!= null) {
                    gotoScreen(goto)
                }
            }}
        )
    }

    private fun gotoScreen(goto: SplashActivity.Companion.GOTO) {
        bottomNav.menu.getItem(0).isCheckable= true
        when (goto) {
            SplashActivity.Companion.GOTO.PROFILE -> {
                logger.debug("Mostramos el perfil...")
                if (supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)== null) {
                    //supportActionBar?.title= resources.getString(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance(Bundle()), ProfileFragment::class.java.simpleName).commit()
                }
                /*if (goto== SplashActivity.Companion.GOTO.PROFILE_PROFILE_NEEDED) {
                    showMessageDialog(R.string.profile_is_needed, R.string.advertisement)
                }*/
            }
            SplashActivity.Companion.GOTO.HOME -> {
                logger.debug("Mostramos el home...")
                if (supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)== null) {
                    supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment.newInstance(Bundle()), HomeFragment::class.java.simpleName).commit()
                }
                //supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment.newInstance(Bundle()), HomeFragment::class.java.simpleName).commit()
            }
            SplashActivity.Companion.GOTO.NOTIFICATIONS -> {
                logger.debug("Mostramos notificaciones...")
                if (supportFragmentManager.findFragmentByTag(HomeNotificationsFragment::class.java.simpleName)== null) {
                    supportActionBar?.title = resources.getString(R.string.notifications_title)
                    supportFragmentManager.beginTransaction().replace(R.id.container, HomeNotificationsFragment.newInstance(Bundle()), HomeNotificationsFragment::class.java.simpleName).commit()
                }
            }
        }
    }

    /*private fun showMessageDialog(@StringRes message: Int, @StringRes title: Int ) {
        val alertDialog= AlertDialog.newInstanceForMessage(getString(title), getString(message), getString(R.string.agree), { })
        val ft = supportFragmentManager.beginTransaction()
        val prev =   supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        alertDialog.show(ft, FRAGMENT_TAG)
    }*/

    private fun finishMeInAwhile(delay: Long) {
        if (!isEmailVerified) {
            Handler().postDelayed({
                finish()
            }, delay)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEmailVerified) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu_home, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_profile -> {
                /*if (viewModel.user.isProfilePendingToInput(this)) {
                    viewModel.updateGoto(SplashActivity.Companion.GOTO.PROFILE_PROFILE_NEEDED)
                } else {*/
                    viewModel.updateGoto(SplashActivity.Companion.GOTO.PROFILE)
                //}
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        logger.debug("onPause...")
    }

    override fun onResume() {
        super.onResume()
        logger.debug("onResume...")
    }

    companion object {
        private const val IS_EMAIL_VERIFIED_KEY= "is_email_verified"
        private const val GOTO_KEY= "GOTO"

        fun startActivity(isEmailVerified: Boolean, goto: Int= -1, context: Context) {
            val bundle= Bundle().apply {
                putBoolean(IS_EMAIL_VERIFIED_KEY, isEmailVerified)
                putInt(GOTO_KEY, goto)
            }
            context.startActivity(Intent(context, HomeActivity::class.java).also {
                it.putExtras(bundle)
                it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}
