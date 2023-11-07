package tn.amin.mpro2.tasker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.condition.TaskerPluginRunnerConditionEvent
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.extensions.requestQuery
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputObject
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultCondition
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultConditionSatisfied
import tn.amin.mpro2.R

// TaskerPluginRunnerConditionEvent<OnMessageInput, OnMessageOutput, OnMessageUpdate>

class OnMessageActionRunner : TaskerPluginRunnerConditionEvent<OnMessageInput, OnMessageOutput, OnMessageUpdate>() {
    override fun getSatisfiedCondition(context: Context, input: TaskerInput<OnMessageInput>, update: OnMessageUpdate?): TaskerPluginResultCondition<OnMessageOutput> {
        val message = update?.message ?: "(void)";
        val messageId = update?.messageId ?: "";
        val senderThreadKey = update?.senderThreadKey ?: "";
        val convThreadKey = update?.convThreadKey ?: -1L;
        return TaskerPluginResultConditionSatisfied(context, OnMessageOutput(message, messageId, senderThreadKey, convThreadKey))
    }
}

class OnMessageEventHelper(config: TaskerPluginConfig<OnMessageInput>) : TaskerPluginConfigHelper<OnMessageInput, OnMessageOutput, OnMessageActionRunner>(config) {
//    override fun addToStringBlurb(input: TaskerInput<Unit>, blurbBuilder: StringBuilder) {
//        blurbBuilder.append("Fires up when a message is received")
//    }

    override val inputClass: Class<OnMessageInput> = OnMessageInput::class.java
    override val outputClass: Class<OnMessageOutput> = OnMessageOutput::class.java
    override val runnerClass: Class<OnMessageActionRunner> = OnMessageActionRunner::class.java
}

@TaskerInputRoot
class OnMessageInput

@TaskerOutputObject
class OnMessageOutput constructor(
        @get:TaskerOutputVariable("last_message", R.string.last_message, R.string.last_message_description) val message: String,
        @get:TaskerOutputVariable("last_message_id", R.string.last_message_id, R.string.last_message_id_description) val messageId: String,
        @get:TaskerOutputVariable("last_message_sender_threadkey", R.string.last_message_sender_threadkey, R.string.last_message_sender_threadkey_description) val senderThreadKey: String,
        @get:TaskerOutputVariable("last_message_conv_threadkey", R.string.last_message_conv_threadkey, R.string.last_message_conv_threadkey_description) val convThreadKey: Long,
)

@TaskerInputRoot
class OnMessageUpdate @JvmOverloads constructor(
        @field:TaskerInputField("message") var message: String? = null,
        @field:TaskerInputField("message_id") var messageId: String? = null,
        @field:TaskerInputField("sender_threadkey") var senderThreadKey: String? = null,
        @field:TaskerInputField("conv_threadkey") var convThreadKey: Long? = null
)

class ActivityOnMessageConfig : Activity(), TaskerPluginConfig<OnMessageInput> {
    override val context get() = applicationContext
    override val inputForTasker: TaskerInput<OnMessageInput> = TaskerInput(OnMessageInput())

    override fun assignFromInput(input: TaskerInput<OnMessageInput>) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OnMessageEventHelper(this).finishForTasker()
    }

    companion object {
        @JvmStatic
        fun triggerMessageReceived(context: Context, message: String, messageId: String, senderThreadKey: String, convThreadKey: Long) =
                ActivityOnMessageConfig::class.java.requestQuery(context, OnMessageUpdate(message, messageId, senderThreadKey, convThreadKey))
    }
}
