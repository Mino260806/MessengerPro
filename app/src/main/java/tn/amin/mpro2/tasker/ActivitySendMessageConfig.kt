package tn.amin.mpro2.tasker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import org.apache.commons.lang3.math.NumberUtils
import tn.amin.mpro2.orca.OrcaBridge
import tn.amin.mpro2.R
import java.lang.NullPointerException

class ActivitySendMessageConfig : Activity(), TaskerPluginConfig<SendMessageInput> {
    override val context get() = applicationContext
    override val inputForTasker: TaskerInput<SendMessageInput>
        get() = TaskerInput(SendMessageInput(
                messageEdit.text.toString(),
                threadKeyEdit.text.toString(),
                replyMessageIdEdit.text.toString(),
        ))
    private val taskerHelper by lazy { BasicActionHelper(this) }

    private lateinit var buttonConfirm: Button;
    private lateinit var messageEdit: EditText
    private lateinit var threadKeyEdit: EditText
    private lateinit var replyMessageIdEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message_config)

        messageEdit = findViewById(R.id.edit_message)
        threadKeyEdit = findViewById(R.id.edit_threadkey)
        replyMessageIdEdit = findViewById(R.id.edit_reply_id)
        buttonConfirm = findViewById(R.id.button_confirm)

        buttonConfirm.setOnClickListener {
            taskerHelper.finishForTasker()
        }
    }

    override fun assignFromInput(input: TaskerInput<SendMessageInput>) {
        messageEdit.setText(input.regular.message.toString())
        threadKeyEdit.setText(input.regular.threadKey.toString())
        replyMessageIdEdit.setText(input.regular.replyMessageId.toString())
    }
}

@TaskerInputRoot
class SendMessageInput @JvmOverloads constructor(
        @field:TaskerInputField("message") var message: String? = null,
        @field:TaskerInputField("threadkey") var threadKey: String? = null,
        @field:TaskerInputField("reply_message_id") var replyMessageId: String? = null
)

class BasicActionHelper(config: TaskerPluginConfig<SendMessageInput>) : TaskerPluginConfigHelperNoOutput<SendMessageInput, BasicActionRunner>(config) {
    override val inputClass: Class<SendMessageInput> get() = SendMessageInput::class.java
    override val runnerClass: Class<BasicActionRunner> get() = BasicActionRunner::class.java
    override fun addToStringBlurb(input: TaskerInput<SendMessageInput>, blurbBuilder: StringBuilder) {
    }
}

class BasicActionRunner : TaskerPluginRunnerActionNoOutput<SendMessageInput>() {
    override fun run(context: Context, input: TaskerInput<SendMessageInput>): TaskerPluginResult<Unit> {
        Handler(Looper.getMainLooper()).post { Toast.makeText(context, "Sending message...", Toast.LENGTH_LONG).show() }

        input.regular.threadKey ?: return TaskerPluginResultError(NullPointerException())
        input.regular.message ?: return TaskerPluginResultError(NullPointerException())

        val threadKey = NumberUtils.toLong(input.regular.threadKey, -1L);
        if (threadKey == -1L) return TaskerPluginResultError(NullPointerException())

        OrcaBridge.sendMessage(context, input.regular.message, input.regular.replyMessageId, threadKey)
        return TaskerPluginResultSucess()
    }
}
