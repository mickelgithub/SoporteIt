package es.samiralkalii.myapps.soporteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.soporteit.ui.UiModel
import es.samiralkalii.myapps.soporteit.ui.util.Event

class HomeViewModelUiModel: UiModel {

    val _emailValidated= MutableLiveData<Boolean>()
    val emailValidated: LiveData<Boolean>
        get() = _emailValidated

    val _navTo= MutableLiveData<Event<Int>>()
    val navTo: LiveData<Event<Int>>
        get() = _navTo

    val _bottomNavEnabled= MutableLiveData(true)
    val bottomNavEnabled: LiveData<Boolean>
        get()= _bottomNavEnabled
}