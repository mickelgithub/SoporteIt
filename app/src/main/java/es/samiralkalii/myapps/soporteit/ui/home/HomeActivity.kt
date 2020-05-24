package es.samiralkalii.myapps.soporteit.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ActivityHomeBinding
import es.samiralkalii.myapps.soporteit.ui.BaseActivity
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivityViewModel.Companion.CONFIRMED_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivityViewModel.Companion.IS_EMAIL_VALIDATED_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivityViewModel.Companion.NAVIGATE_TO_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentDirections
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

private const val DELAY_WHEN_NO_EMAIL_VERIFIED= 7000L

class HomeActivity : BaseActivity() {

    private val logger= LoggerFactory.getLogger(HomeActivity::class.java)

    override val viewModel: HomeActivityViewModel by viewModel()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val appBacConfiguration by lazy {
        AppBarConfiguration(setOf(
            R.id.homeFragment, R.id.notificationsFragment,
            R.id.schedulersFragment, R.id.holidaysFragment,
            R.id.absencesFragment, R.id.profileFragmentTopLevel))
    }

    override fun initUI() {

        binding= ActivityHomeBinding.inflate(layoutInflater).apply {
            uiModel= viewModel.uiModel
            lifecycleOwner= this@HomeActivity
        }
        setContentView(binding.root)
        binding.executePendingBindings()
        toolbar.title= ""
        setSupportActionBar(toolbar)
        setupNavigationController()
        setupToolbarNavigationView()
        setupBottomNavigationView()

    }

    private fun setupNavigationController() {
        navController= findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id) {
                R.id.homeFragment -> {
                    val args1 = NavArgument.Builder()
                        .setDefaultValue(viewModel.uiModel.emailValidated.value).build()
                    val args2 =
                        NavArgument.Builder().setDefaultValue(viewModel.uiModel.confirmed.value)
                            .build()
                    destination.addArgument(IS_EMAIL_VALIDATED_BUNDLE_KEY, args1)
                    destination.addArgument(CONFIRMED_BUNDLE_KEY, args2)
                }
            }
            disableEnableBottonNavViewOption(destination.id)
        }
        navController.setGraph(navController.graph, bundleOf(IS_EMAIL_VALIDATED_BUNDLE_KEY to intent.extras!!.getBoolean(IS_EMAIL_VALIDATED_BUNDLE_KEY, false),
        CONFIRMED_BUNDLE_KEY to intent.extras!!.getBoolean(CONFIRMED_BUNDLE_KEY, false)))
    }

    private fun disableEnableBottonNavViewOption(@IdRes id: Int) {
        bottomNav.menu.iterator().forEach {
            it.isEnabled= it.itemId!= id
        }
    }

    private fun setupToolbarNavigationView() {
        toolbar.setupWithNavController(navController, appBacConfiguration)
    }

    private fun setupBottomNavigationView() {
        bottomNav.setupWithNavController(navController)
    }

    override fun initStateObservation() {

        viewModel.uiModel.emailValidated.observe(this, Observer {
            if (!it) {
                finishMeInAwhile()
            }
        })

        viewModel.uiModel.navTo.observe(this, Observer {
            logger.debug("hey....")
            it.let {event ->
                event.getContentIfNotHandled().let { navTo ->
                    if (navTo!= 0) {
                        gotoScreen(navTo!!)
                    }
                }
            }
        })
    }


    private fun gotoScreen(navTo: Int) {
        //bottomNav.menu.getItem(0).isCheckable= true

        when (navTo) {
            R.id.profileFragmentTopLevel -> {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragmentTopLevel())
            }
            R.id.homeFragment -> {
                //nothing because is the the startDestination
                disableEnableBottonNavViewOption(navTo)
            }
            else -> {
                navController.navigate(navTo)
            }
        }
    }

    private fun finishMeInAwhile() {
        Handler().postDelayed({
            finish()
        }, DELAY_WHEN_NO_EMAIL_VERIFIED)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!navController.popBackStack()) {
            logger.debug("No hay mas en el backStack, finalizamos")
            finish()
            return true
        }
        return super.onSupportNavigateUp()
    }

    companion object {

        fun startActivity(context: Context, isEmailValidated: Boolean, isConfirmed: Boolean, extras: Bundle?= null) {
            val bundle= Bundle().apply {
                putBoolean(IS_EMAIL_VALIDATED_BUNDLE_KEY, isEmailValidated)
                putBoolean(CONFIRMED_BUNDLE_KEY, isConfirmed)
                extras?.let {
                    putInt(NAVIGATE_TO_BUNDLE_KEY, it.getInt(NAVIGATE_TO_BUNDLE_KEY))
                }
            }
            context.startActivity(Intent(context, HomeActivity::class.java).also {
                it.putExtras(bundle)
                it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        fun getIntentToProfileScreen(context: Context)=
            Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(NAVIGATE_TO_BUNDLE_KEY, R.id.profileFragment)

        fun getIntentForHome(context: Context)= Intent(context, SplashActivity::class.java)

        fun getIntentToNotificationsScreen(context: Context)=
            Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(NAVIGATE_TO_BUNDLE_KEY, R.id.notificationsFragment)
    }
}
