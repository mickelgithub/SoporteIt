package es.samiralkalii.myapps.soporteit.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.parcel.Parcelize
import org.slf4j.LoggerFactory


@Parcelize
enum class DIALOG_TYPE: Parcelable {
    MESSAGE, TEXT_INPUT
}

const val DIALOG_TYPE_KEY= "dialog_type_key"
const val DIALOG_TITLE_KEY= "dialog_title_key"
const val DIALOG_MESSAGE_KEY= "dialog_message_key"

class AlertDialog: DialogFragment() {

    private val logger = LoggerFactory.getLogger(AlertDialog::class.java)

    private lateinit var onValue: (String) -> Unit

    companion object {

        fun newInstanceForMessage(title: String, message: String)= AlertDialog().apply {
            arguments= Bundle().apply {
                putParcelable(DIALOG_TYPE_KEY, DIALOG_TYPE.MESSAGE)
                putString(DIALOG_TITLE_KEY, title)
                putString(DIALOG_MESSAGE_KEY, message)
            }
        }

        fun newInstanceForInput(title: String, onValueParam:(inputText: String) -> Unit)= AlertDialog().apply {
            arguments= Bundle().apply {
                putParcelable(DIALOG_TYPE_KEY, DIALOG_TYPE.TEXT_INPUT)
                putString(DIALOG_TITLE_KEY, title)
            }
            onValue= onValueParam
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args= arguments!!

        val dialogType= args.getParcelable(DIALOG_TYPE_KEY) as DIALOG_TYPE
        val title= args.getString(DIALOG_TITLE_KEY)

        when (dialogType) {
            DIALOG_TYPE.MESSAGE -> {
                return getAlertDialog(activity as Context, title, args.getString(DIALOG_MESSAGE_KEY), null)
            }
            DIALOG_TYPE.TEXT_INPUT -> {
                val input = EditText(activity)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                input.layoutParams = lp
                return getAlertDialog(activity as Context, title, null, input)
            }
        }
    }

    private fun getAlertDialog(context: Context, title: String, message: String?, view: View?): Dialog {
        val builder= AlertDialog.Builder(context)
        builder.setTitle(title)
        view?.let {
            builder.setView(it)
        }
        message?.let {
            builder.setMessage(message)
        }
        builder.setPositiveButton(android.R.string.ok) {
            dialog, _ -> if (view!= null) {
                val inputText= (view as EditText).text.toString()
                if (inputText.isNotBlank()) {
                    onValue((view as EditText).text.toString())
                }
            }
        }
        return builder.create()

    }
}