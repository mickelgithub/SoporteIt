package es.samiralkalii.myapps.soporteit.ui

import androidx.lifecycle.ViewModel

abstract class BaseFragmentViewModel: ViewModel() {

    abstract val uiModel: UiModel?
    abstract fun init()
}