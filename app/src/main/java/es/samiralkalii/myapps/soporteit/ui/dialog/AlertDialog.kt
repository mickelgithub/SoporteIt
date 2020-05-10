package es.samiralkalii.myapps.soporteit.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.samiralkalii.myapps.soporteit.R
import kotlinx.android.parcel.Parcelize
import org.slf4j.LoggerFactory


@Parcelize
enum class DIALOG_TYPE: Parcelable {
    MESSAGE, TEXT_INPUT
}

const val DIALOG_TYPE_KEY= "dialog_type_key"
const val DIALOG_TITLE_KEY= "dialog_title_key"
const val DIALOG_MESSAGE_KEY= "dialog_message_key"
const val FRAGMENT_TAG= "dialog_interaction"

class AlertDialog: DialogFragment() {

    private val logger = LoggerFactory.getLogger(AlertDialog::class.java)

    private lateinit var onValueInput: (String) -> Unit
    private var onPositiveButtonClick: (() -> Unit)?= null
    private var positiveButtonText: String?= null
    private var onNegativeButtonClick: (() -> Unit)?= null
    private var negativeButtonText: String?= null


    companion object {

        fun newInstanceForMessage(title: String, message: String,
                                  positiveButtonText: String?,
                                  onPositiveButtonClick: (() -> Unit)?,
                                  negativeButtonText: String?= null,
                                  onNegativeButtonClick:(() -> Unit)?= null)= AlertDialog().apply {
            arguments= Bundle().apply {
                putParcelable(DIALOG_TYPE_KEY, DIALOG_TYPE.MESSAGE)
                putString(DIALOG_TITLE_KEY, title)
                putString(DIALOG_MESSAGE_KEY, message)
            }
            this@apply.onPositiveButtonClick= onPositiveButtonClick
            this@apply.positiveButtonText= positiveButtonText
            this@apply.onNegativeButtonClick= onNegativeButtonClick
            this@apply.negativeButtonText= negativeButtonText
        }

        fun newInstanceForInput(title: String, onValue:(inputText: String) -> Unit)= AlertDialog().apply {
            arguments= Bundle().apply {
                putParcelable(DIALOG_TYPE_KEY, DIALOG_TYPE.TEXT_INPUT)
                putString(DIALOG_TITLE_KEY, title)
            }
            this@apply.onValueInput= onValue
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args= requireArguments()

        val dialogType= args.getParcelable<DIALOG_TYPE>(DIALOG_TYPE_KEY)
        val title= args.getString(DIALOG_TITLE_KEY) ?: ""

        when (dialogType) {
            DIALOG_TYPE.MESSAGE -> {
                return getAlertDialog(activity as Context, title, args.getString(DIALOG_MESSAGE_KEY), null)
            }
            else -> {
                val input = EditText(activity)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                input.layoutParams = lp
                input.inputType= InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                input.requestFocus()
                return getAlertDialog(activity as Context, title, null, input)
            }
        }
    }

    private fun getAlertDialog(context: Context, title: String, message: String?, view: View?): Dialog {
        val builder= MaterialAlertDialogBuilder(context, R.style.MyDialogCustomTheme)
        builder.setTitle(title)
        view?.let {
            builder.setView(it)
        }
        message?.let {
            builder.setMessage(message)
            builder.setCancelable(false)
        }
        builder.setPositiveButton(positiveButtonText ?: context.resources.getString(android.R.string.ok)) {
            _, _ -> view?.let {
                if (view is EditText) {
                    val inputText= view.text.toString()
                    if (inputText.isNotBlank()) {
                        onValueInput(view.text.toString())
                    }
                }
            }
            onPositiveButtonClick?.let {
                it()
                dismiss()
            }
        }
        negativeButtonText?.let {
            builder.setNegativeButton(it) {
                    _, _ ->
                onNegativeButtonClick?.let {
                    it()
                    dismiss()
                }
            }
        }
        return builder.create()

    }

}

fun AppCompatActivity.showDialog(dialog: es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog) {
    val ft = supportFragmentManager.beginTransaction()
    val prev =   supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
    if (prev != null) {
        ft.remove(prev)
    }
    ft.addToBackStack(null)
    dialog.show(ft, FRAGMENT_TAG)
}