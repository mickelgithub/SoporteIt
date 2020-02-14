package es.samiralkalii.myapps.soporteit.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.BR

abstract class RecyclerBaseAdapter : RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), viewType, parent, false))

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        getViewModel(position)
            ?.let {
                val bindingSuccess = holder.binding.setVariable(BR.viewModel, it)
                if (!bindingSuccess) {
                    throw IllegalStateException("Binding ${holder.binding} viewModel variable name should be 'viewModel'")
                }
            }
    }

    override fun getItemViewType(position: Int) = getLayoutIdForPosition(position)

    abstract fun getLayoutIdForPosition(position: Int): Int

    abstract fun getViewModel(position: Int): Any?

}

open class RecyclerViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)