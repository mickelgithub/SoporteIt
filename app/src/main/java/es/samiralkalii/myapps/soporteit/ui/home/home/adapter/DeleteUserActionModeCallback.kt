package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel

class DeleteUserActionModeCallback(val adapter: MemberUserAdapter, homeFragmentViewModel: HomeFragmentViewModel): ActionMode.Callback  {

    private var multiSelect= false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            it.menuInflater.inflate(R.menu.menu_action_mode_home_fragment, menu)
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {

    }

}