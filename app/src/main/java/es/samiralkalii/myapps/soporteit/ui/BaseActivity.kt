package es.samiralkalii.myapps.soporteit.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.slf4j.LoggerFactory

abstract class BaseActivity : AppCompatActivity() {

    private val logger = LoggerFactory.getLogger(BaseActivity::class.java)

    internal abstract val viewModel: BaseViewModel

    abstract fun initUI()
    abstract fun initStateObservation()
    open fun initLoading(data: Bundle?= null) {
        viewModel.init(data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("${this.javaClass.simpleName} onCreate******")
        initLoading(intent.extras)
        initUI()
        initStateObservation()
    }

    override fun onStart() {
        super.onStart()
        logger.debug("${this.javaClass.simpleName} onStart******")
    }

    override fun onResume() {
        super.onResume()
        logger.debug("${this.javaClass.simpleName} onResume******")
    }

    override fun onPause() {
        super.onPause()
        logger.debug("${this.javaClass.simpleName} onPause******")
    }

    override fun onStop() {
        super.onStop()
        logger.debug("${this.javaClass.simpleName} onStop******")
    }

    override fun onRestart() {
        super.onRestart()
        logger.debug("${this.javaClass.simpleName} onRestart******")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("${this.javaClass.simpleName} onDestroy******")
    }
}