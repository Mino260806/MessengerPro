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

class ActivityReactToMessageConfig : Activity(), TaskerPluginConfig<ReactToMessageInput> {
    override val context get() = applicationContext
    override val inputForTasker: TaskerInput<ReactToMessageInput>
        get() = TaskerInput(ReactToMessageInput(
                reactionEdit.text.toString(),
                messageIdEdit.text.toString(),
                threadKeyEdit.text.toString(),
        ))
    private val taskerHelper by lazy { ReactToMessageActionHelper(this) }

    private lateinit var buttonConfirm: Button;
    private lateinit var reactionEdit: EditText
    private lateinit var messageIdEdit: EditText
    private lateinit var threadKeyEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_react_to_message_config)

        reactionEdit = findViewById(R.id.edit_reaction)
        messageIdEdit = findViewById(R.id.edit_message_id)
        threadKeyEdit = findViewById(R.id.edit_threadkey)
        buttonConfirm = findViewById(R.id.button_confirm)

        buttonConfirm.setOnClickListener {
            taskerHelper.finishForTasker()
        }
    }

    override fun assignFromInput(input: TaskerInput<ReactToMessageInput>) {
        reactionEdit.setText(input.regular.reaction.toString())
        messageIdEdit.setText(input.regular.messageId.toString())
        threadKeyEdit.setText(input.regular.threadKey.toString())
    }
}

@TaskerInputRoot
class ReactToMessageInput @JvmOverloads constructor(
        @field:TaskerInputField("reaction") var reaction: String? = null,
        @field:TaskerInputField("message_id") var messageId: String? = null,
        @field:TaskerInputField("threadkey") var threadKey: String? = null
)

class ReactToMessageActionHelper(config: TaskerPluginConfig<ReactToMessageInput>) : TaskerPluginConfigHelperNoOutput<ReactToMessageInput, ReactToMessageActionRunner>(config) {
    override val inputClass: Class<ReactToMessageInput> get() = ReactToMessageInput::class.java
    override val runnerClass: Class<ReactToMessageActionRunner> get() = ReactToMessageActionRunner::class.java
    override fun addToStringBlurb(input: TaskerInput<ReactToMessageInput>, blurbBuilder: StringBuilder) {

//        blurbBuilder.append("\nSends a message saying \"\" to user ${input.regular.threadKey}")
//        Handler(Looper.getMainLooper()).post { Toast.makeText(context, "Basic", Toast.LENGTH_LONG).show() }
    }
}

class ReactToMessageActionRunner : TaskerPluginRunnerActionNoOutput<ReactToMessageInput>() {
    override fun run(context: Context, input: TaskerInput<ReactToMessageInput>): TaskerPluginResult<Unit> {
        Handler(Looper.getMainLooper()).post { Toast.makeText(context, "Reacting to message...", Toast.LENGTH_LONG).show() }

        input.regular.reaction ?: return TaskerPluginResultError(NullPointerException())
        input.regular.messageId ?: return TaskerPluginResultError(NullPointerException())
        input.regular.threadKey ?: return TaskerPluginResultError(NullPointerException())

        val threadKey = NumberUtils.toLong(input.regular.threadKey, -1L);
        if (threadKey == -1L) return TaskerPluginResultError(NullPointerException())

        OrcaBridge.reactToMessage(context, input.regular.reaction, input.regular.messageId, threadKey)
        return TaskerPluginResultSucess()
    }
}
