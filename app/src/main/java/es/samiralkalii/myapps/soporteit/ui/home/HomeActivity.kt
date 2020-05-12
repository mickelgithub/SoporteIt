package es.samiralkalii.myapps.soporteit.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityHomeBinding
import es.samiralkalii.myapps.soporteit.ui.BaseActivity
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel.Companion.IS_EMAIL_VALIDATED_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel.Companion.NAVIGATE_TO_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class HomeActivity : BaseActivity() {

    private val logger= LoggerFactory.getLogger(HomeActivity::class.java)

    override val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val appBacConfiguration by lazy {
        AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.notificationsFragment, R.id.schedulersFragment, R.id.holidaysFragment,
        R.id.absencesFragment))
    }

    override fun initUI() {

        binding= ActivityHomeBinding.inflate(layoutInflater).apply {
            uiModel= viewModel.uiModel
            lifecycleOwner= this@HomeActivity
        }
        setContentView(binding.root)
        binding.executePendingBindings()

        setSupportActionBar(toolbar)
        setupNavigationController()
        setupToolbarNavigationView()
        setupBottomNavigationView()

    }

    private fun setupNavigationController() {
        navController= findNavController(R.id.nav_host_fragment)
        navController.setGraph(navController.graph, bundleOf(IS_EMAIL_VALIDATED_BUNDLE_KEY to intent.extras!!.getBoolean(IS_EMAIL_VALIDATED_BUNDLE_KEY, false)))
    }

    private fun setupToolbarNavigationView() {
        toolbar.setupWithNavController(navController, appBacConfiguration)
    }

    private fun setupBottomNavigationView() {
        bottomNav.setupWithNavController(navController)
        /*bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_item_absence -> {
                    viewModel.updateGoto(HomeViewModel.GOTO.ABSENCES)
                    true
                }
                R.id.menu_item_holidays -> {
                    viewModel.updateGoto(HomeViewModel.GOTO.HOLIDAYS)
                    true
                }
                R.id.menu_item_notifications -> {
                    viewModel.updateGoto(HomeViewModel.GOTO.NOTIFICATIONS)
                    true
                }
                R.id.menu_item_schedulers -> {
                    toast("Pronto tendremos esta funcionalidad de crear planificaciones")
                    true
                }
                R.id.menu_item_home -> {
                    viewModel.updateGoto(HomeViewModel.GOTO.HOME)
                    true
                }
                else -> {
                    false
                }
            }
        }*/
    }

    override fun initStateObservation() {

        viewModel.uiModel.emailValidated.observe(this, Observer {
            if (!it) {
                //supportActionBar?.title= getString(R.string.app_title_need_mail_validation)
                finishMeInAwhile(7000L)
            }
        })

        viewModel.uiModel.navTo.observe(this, Observer {
            it.getContentIfNotHandled().let { navTo ->
                if (navTo!= null) {
                    gotoScreen(navTo)
                }
            }}
        )
    }


    private fun gotoScreen(navTo: Int) {
        bottomNav.menu.getItem(0).isCheckable= true
        when (navTo) {
            R.id.profileFragment -> {
                logger.debug("Mostramos el perfil...")
                navController.navigate(R.id.action_homeFragment_to_profileFragment)
                /*if (supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)== null) {
                    //supportActionBar?.title= resources.getString(R.string.profile)
                    supportFragmentManager.beginTransaction().replace(R.id.container, ProfileFragment.newInstance(Bundle()), ProfileFragment::class.java.simpleName).commit()
                }*/
            }
            /*HomeViewModel.GOTO.HOME -> {
                logger.debug("Mostramos el home...")
                if (supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)== null) {
                    supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment.newInstance(Bundle()), HomeFragment::class.java.simpleName).commit()
                }
                //supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment.newInstance(Bundle()), HomeFragment::class.java.simpleName).commit()
            }
            HomeViewModel.GOTO.NOTIFICATIONS -> {
                logger.debug("Mostramos notificaciones...")
                if (supportFragmentManager.findFragmentByTag(HomeNotificationsFragment::class.java.simpleName)== null) {
                    supportActionBar?.title = resources.getString(R.string.notifications_title)
                    supportFragmentManager.beginTransaction().replace(R.id.container, HomeNotificationsFragment.newInstance(Bundle()), HomeNotificationsFragment::class.java.simpleName).commit()
                }
            }*/
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
        Handler().postDelayed({
            finish()
        }, delay)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        viewModel.uiModel.emailValidated.value?.let {
            if (it) {
                val inflater: MenuInflater = menuInflater
                inflater.inflate(R.menu.menu_home, menu)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    companion object {

        fun startActivity(context: Context, isEmailValidated: Boolean, extras: Bundle?= null) {
            val bundle= Bundle().apply {
                putBoolean(IS_EMAIL_VALIDATED_BUNDLE_KEY, isEmailValidated)
                extras?.let {
                    putInt(NAVIGATE_TO_BUNDLE_KEY, it.getInt(NAVIGATE_TO_BUNDLE_KEY))
                }
            }
            context.startActivity(Intent(context, HomeActivity::class.java).also {
                it.putExtras(bundle)
                it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        fun getIntentToProfileScreen(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(NAVIGATE_TO_BUNDLE_KEY, R.id.profileFragment)
            return intent
        }

        fun getIntentForHome(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java)
            return intent
        }

        fun getIntentToNotificationsScreen(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(NAVIGATE_TO_BUNDLE_KEY, R.id.notificationsFragment)
            return intent
        }
    }
}
