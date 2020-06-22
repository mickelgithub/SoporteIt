package es.samiralkalii.myapps.soporteit.ui.util.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserAdapter
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import org.slf4j.LoggerFactory

class CustomDividerItemDecoration(val context: Context): RecyclerView.ItemDecoration() {

    private val logger= LoggerFactory.getLogger(CustomDividerItemDecoration::class.java)

    private var dividerDrawable: Drawable

    init {
        dividerDrawable= context.getDrawable(R.drawable.drawable_divider)!!
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount

        for ( i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position= parent.getChildAdapterPosition(child)
            if (position< (parent.adapter as MemberUserAdapter).members.size && position>= 0) {
                val item= (parent.adapter as MemberUserAdapter).members[position]
                if (item is MemberUserViewModelTemplate.GroupMemberUserViewModel && !item.isExpanded && (parent.getChildAdapterPosition(child) != parent.adapter!!.itemCount - 1)) {
                    val params = child.layoutParams as RecyclerView.LayoutParams


                    val top = child.bottom + params.bottomMargin;
                    val bottom = top + dividerDrawable.getIntrinsicHeight();

                    dividerDrawable.setBounds(left, top, right, bottom);

                    /*if ((parent.getChildAdapterPosition(child) == parent.adapter!!.itemCount - 1) && parent.bottom < bottom) { // this prevent a parent to hide the last item's divider
                        parent.setPadding(parent.paddingLeft, parent.paddingTop, parent.paddingRight, bottom - parent.bottom);
                    }*/
                    dividerDrawable.draw(c);
                }
            }

        }
    }
}