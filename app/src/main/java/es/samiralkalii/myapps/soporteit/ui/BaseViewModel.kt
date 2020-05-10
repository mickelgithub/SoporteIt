package es.samiralkalii.myapps.soporteit.ui

import android.os.Bundle
import androidx.lifecycle.ViewModel

abstract class BaseViewModel: ViewModel() {

    abstract val uiModel: UiModel?
    abstract fun init(data: Bundle?= null)
}