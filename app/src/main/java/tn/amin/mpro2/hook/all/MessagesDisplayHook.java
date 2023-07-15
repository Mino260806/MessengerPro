package tn.amin.mpro2.hook.all;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
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
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        return Collections.singleton(XposedBridge.hookMethod(
                gateway.unobfuscator.getMethod(OrcaUnobfuscator.METHOD_MESSAGES_DECODER_DECODE),
                wrap(new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object messagesCollection = param.getResult();
                MessagesCollectionWrapper messagesCollectionWrapper = new MessagesCollectionWrapper(gateway, messagesCollection);
                List<?> messageList = messagesCollectionWrapper.getList();
                for (int i=0; i<messageList.size(); i++) {
                    Object message = messageList.get(i);
                    if (message == null) continue;

                    MessageWrapper messageWrapper = new MessageWrapper(gateway, message);

                    final int index = i;
                    notifyListeners((listener) -> ((MessageDisplayHookListener) listener).onMessageDisplay(
                            messageWrapper, index, messageList.size(), messagesCollectionWrapper));
                }
            }
        })));
    }

    public interface MessageDisplayHookListener {
        void onMessageDisplay(MessageWrapper message, int index, int count, MessagesCollectionWrapper messagesCollection);
    }
}
