package tn.amin.mpro2.hook.all;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.HookTime;
import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.wrapper.MessageWrapper;
import tn.amin.mpro2.orca.wrapper.MessagesCollectionWrapper;

public class MessagesDisplayHook extends BaseHook {
    @Override
    public HookId getId() {
        return HookId.MESSAGES_DISPLAY;
    }

    @Override
    public HookTime getHookTime() {
        return HookTime.AFTER_DEOBFUSCATION;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        return Collections.singleton(XposedBridge.hookMethod(
                gateway.unobfuscator.getMethod(OrcaUnobfuscator.METHOD_MESSAGES_DECODER_DECODE),
                wrap(new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object messagesCollection = param.getResult();
                if (messagesCollection == null) return;

                MessagesCollectionWrapper messagesCollectionWrapper = new MessagesCollectionWrapper(gateway, messagesCollection);
                List<?> messageList = messagesCollectionWrapper.getList();
                for (int i=0; i<messageList.size(); i++) {
                    Object message = messageList.get(i);

                    MessageWrapper messageWrapper;
                    if (message != null)
                        messageWrapper = new MessageWrapper(gateway, message);
                    else
                        messageWrapper = null;

                    final int index = i;
                    notifyListeners((listener) -> ((MessageDisplayHookListener) listener).onMessageDisplay(
                            messageWrapper, index, messageList.size(), messagesCollectionWrapper));
                }
            }
        })));
    }

    public interface MessageDisplayHookListener {
        void onMessageDisplay(@Nullable MessageWrapper message, int index, int count, MessagesCollectionWrapper messagesCollection);
    }
}
