package es.samiralkalii.myapps.soporteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.soporteit.ui.UiModel
import es.samiralkalii.myapps.soporteit.ui.util.Event

class HomeActivityUiModel: UiModel {

    val _emailValidated= MutableLiveData<Boolean>()
    val emailValidated: LiveData<Boolean>
        get() = _emailValidated

    val _confirmed= MutableLiveData<Boolean>()
    val confirmed: LiveData<Boolean>
        get() = _confirmed

    val _navTo= MutableLiveData<Event<Int>>()
    val navTo: LiveData<Event<Int>>
        get() = _navTo

    val _bottomNavEnabled= MutableLiveData(true)
    val bottomNavEnabled: LiveData<Boolean>
        get()= _bottomNavEnabled
}