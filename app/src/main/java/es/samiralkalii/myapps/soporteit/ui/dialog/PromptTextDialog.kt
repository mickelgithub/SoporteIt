package es.samiralkalii.myapps.soporteit.ui.dialog



import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.samiralkalii.myapps.soporteit.databinding.DialogPromptTextBinding
import org.slf4j.LoggerFactory

private const val FRAGMENT_HOST_NAME_ARG_KEY= "fragment_host_name_arg"
private const val MESSAGE_ARG_KEY= "message_arg"
private const val MESSAGE_ERROR_ARG_KEY= "message_error_arg"

class PromptTextDialog(): MyDialog() {

    private val logger = LoggerFactory.getLogger(PromptTextDialog::class.java)

    private lateinit var onTextEntered: OnTextEnteredListener

    private lateinit var binding: DialogPromptTextBinding

    lateinit var messageArg: String
    private lateinit var messageErrorArg: String

    var textEntered: String= ""
    var messageError: String= ""

    companion object {

        fun newInstance(messageArg: String, messageErrorArg: String, onTextEntered: OnTextEnteredListener)= PromptTextDialog().apply {
            this.messageArg= messageArg
            this.messageErrorArg= messageErrorArg
            this.onTextEntered= onTextEntered

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.debug("onAttach....")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        logger.debug("onCreate...")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logger.debug("onCreateView......")
        binding= DialogPromptTextBinding.inflate(inflater, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        return binding.root
    }

    fun onOkClick() {
        logger.debug("el valor del mensage introducido es ${textEntered}")
        if (textEntered.trim().length== 0) {
            messageError= messageErrorArg
            binding.invalidateAll()
        } else {
            onTextEntered.onTextEntered(textEntered.trim())
            dismiss()
        }
    }

    fun onCancelClick() {
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("onViewCreated...")
    }

    interface OnTextEnteredListener {
        fun onTextEntered(input: String)
    }

    override fun onStop() {
        super.onStop()
        logger.debug("OnStop.....")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("onDestroyView...")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("OnDestroy...")
    }
}