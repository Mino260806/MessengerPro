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

class OnUserTypingActionRunner : TaskerPluginRunnerConditionEvent<OnUserTypingInput, OnUserTypingOutput, OnUserTypingUpdate>() {
    override fun getSatisfiedCondition(context: Context, input: TaskerInput<OnUserTypingInput>, update: OnUserTypingUpdate?): TaskerPluginResultCondition<OnUserTypingOutput> {
        val isTyping = update?.isTyping ?: false;
        val userKey = update?.userKey ?: -1L;
        val threadKey = update?.threadKey ?: -1L;
        return TaskerPluginResultConditionSatisfied(context, OnUserTypingOutput(isTyping, userKey, threadKey))
    }
}

class OnUserTypingEventHelper(config: TaskerPluginConfig<OnUserTypingInput>) : TaskerPluginConfigHelper<OnUserTypingInput, OnUserTypingOutput, OnUserTypingActionRunner>(config) {
    override val inputClass: Class<OnUserTypingInput> = OnUserTypingInput::class.java
    override val outputClass: Class<OnUserTypingOutput> = OnUserTypingOutput::class.java
    override val runnerClass: Class<OnUserTypingActionRunner> = OnUserTypingActionRunner::class.java
}

@TaskerInputRoot
class OnUserTypingInput

@TaskerOutputObject
class OnUserTypingOutput constructor(
        @get:TaskerOutputVariable("last_typing_is_typing", R.string.last_typing_is_typing, R.string.last_typing_is_typing_description) val isTyping: Boolean,
        @get:TaskerOutputVariable("last_typing_userkey", R.string.last_typing_userkey, R.string.last_typing_userkey_description) val userKey: Long,
        @get:TaskerOutputVariable("last_typing_threadkey", R.string.last_typing_threadkey, R.string.last_typing_threadkey_description) val threadKey: Long,
)

@TaskerInputRoot
class OnUserTypingUpdate @JvmOverloads constructor(
        @field:TaskerInputField("is_typing") var isTyping: Boolean? = null,
        @field:TaskerInputField("userkey") var userKey: Long? = null,
        @field:TaskerInputField("threadkey") var threadKey: Long? = null
)

class ActivityOnUserTypingConfig : Activity(), TaskerPluginConfig<OnUserTypingInput> {
    override val context get() = applicationContext
    override val inputForTasker: TaskerInput<OnUserTypingInput> = TaskerInput(OnUserTypingInput())

    override fun assignFromInput(input: TaskerInput<OnUserTypingInput>) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OnUserTypingEventHelper(this).finishForTasker()
    }

    companion object {
        @JvmStatic
        fun triggerMessageReceived(context: Context, isTyping: Boolean, userKey: Long, threadKey: Long) =
                ActivityOnUserTypingConfig::class.java.requestQuery(context, OnUserTypingUpdate(isTyping, userKey, threadKey))
    }
}
