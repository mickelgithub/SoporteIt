package es.samiralkalii.myapps.soporteit.ui.home.home.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog

class ConfirmMemberDialog: MyDialog() {



    class ConfirmMemberDialogViewModel: ViewModel() {

        val profile= MutableLiveData<String>("")
        val holidayDays= MutableLiveData<String>("")

        private val _profiles= MutableLiveData<List<String>>(listOf("Programador Junior", "Programador Señor", "Analista Programador"))
        val profiles: LiveData<List<String>>
            get() = _profiles



        private val _email= MutableLiveData<String>("samiralkalii@gmail.com")
        val email: LiveData<String>
            get() = _email

        private val _profileError= MutableLiveData<Int?>()
        val profileError: LiveData<Int?>
            get() = _profileError

        private val _holidayDaysError= MutableLiveData<Int?>()
        val holidayDaysError: LiveData<Int?>
            get() = _holidayDaysError

        private val _showLoading= MutableLiveData<Boolean>(true)
        val showLoading: LiveData<Boolean>
            get() = _showLoading









    }
}