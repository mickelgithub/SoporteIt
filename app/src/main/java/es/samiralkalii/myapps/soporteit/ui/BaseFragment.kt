package es.samiralkalii.myapps.soporteit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.slf4j.LoggerFactory

abstract class BaseFragment: Fragment() {

    private val logger= LoggerFactory.getLogger(BaseFragment::class.java)

    internal abstract val viewModel: BaseFragmentViewModel

    abstract fun initUI(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    abstract fun initStateObservation()
    open fun initLoading() {
        viewModel.init(arguments)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("{$this.javaClass.simpleName} ${this.hashCode()} onCreate******")
        initLoading()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onCreateView******")
        return initUI(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onViewCreated******")
        initStateObservation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onActivityCreated******")
    }

    override fun onStart() {
        super.onStart()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onStart******")
    }

    override fun onResume() {
        super.onResume()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onResume******")
    }

    override fun onPause() {
        super.onPause()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onPause******")
    }

    override fun onStop() {
        super.onStop()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onStop******")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onDestroyView******")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onDestroy******")
    }

}