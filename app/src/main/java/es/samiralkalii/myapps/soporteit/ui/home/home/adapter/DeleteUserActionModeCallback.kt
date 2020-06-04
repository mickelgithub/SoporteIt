package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.showDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel

class DeleteUserActionModeCallback(val adapter: MemberUserAdapter, val fragment: HomeFragment): ActionMode.Callback  {

    private var multiSelect= false
    private val selectedUsers= mutableListOf<String>()
    private val selectedMemberUserViewModel= mutableListOf<MemberUserViewModelTemplate.MemberUserViewModel>()

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?)= when (item?.itemId) {
        R.id.delete_user -> {
            if (selectedUsers.isNotEmpty()) {
                val confirmDialog= AlertDialog.newInstanceForMessage(
                    fragment.resources.getString(R.string.confirm_operacion),
                    fragment.resources.getString(R.string.confirm_users_deletion),
                    fragment.resources.getString(R.string.confirm),
                    {
                        fragment.viewModel.deleteUsers(listOf(*selectedUsers.toTypedArray()))
                        selectedMemberUserViewModel.clear()
                        mode?.finish()
                    },
                    fragment.resources.getString(R.string.cancel),
                    {mode?.finish()}
                )
                (fragment.context as AppCompatActivity).showDialog(confirmDialog)
            }
            true
        }
        else -> false
    }



    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            multiSelect= true
            it.menuInflater.inflate(R.menu.menu_action_mode_home_fragment, menu)
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        multiSelect= false
        selectedUsers.clear()
        selectedMemberUserViewModel.forEach {
            it.select(false)
        }
        selectedMemberUserViewModel.clear()
        //adapter.notifyDataSetChanged()
    }

    fun selectUser(memberUserViewModel: MemberUserViewModelTemplate.MemberUserViewModel) {
        if (multiSelect) {
            if (memberUserViewModel.user.id in selectedUsers) {
                selectedUsers.remove(memberUserViewModel.user.id)
                selectedMemberUserViewModel.remove(memberUserViewModel)
                memberUserViewModel.select(false)
            } else {
                selectedUsers.add(memberUserViewModel.user.id)
                selectedMemberUserViewModel.add(memberUserViewModel)
                memberUserViewModel.select(true)
            }
        }
    }

}