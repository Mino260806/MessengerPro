package tn.amin.mpro2.hook.all;

import java.util.Arrays;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.datatype.Mention;
import tn.amin.mpro2.orca.datatype.TextMessage;

public class MessageSentHook extends BaseHook {
    public static final String DISPATCH_METHOD = "dispatchVIJOOOOOOOOOOOOOOOOOOOOOO";
//    public static final String DISPATCH_METHOD = "dispatchVIJOOOOOOOOOOOOOOOOOOOO";

    public MessageSentHook() {
        super();
    }

    @Override
    public HookId getId() {
        return HookId.MESSAGE_SEND;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        final Class<?> MailboxCoreJNI = XposedHelpers.findClass("com.facebook.core.mca.MailboxCoreJNI", gateway.classLoader);

        return XposedBridge.hookAllMethods(MailboxCoreJNI, DISPATCH_METHOD, wrap(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (param.args[5] instanceof String) {

//                    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName());
//                    logger.warning("sent ! " + Arrays.toString(param.args));

                    Long threadKey = (Long) param.args[2];
                    String message = (String) param.args[5];

                    String rangeStartsString = (String) param.args[7];
                    String rangeEndsString = (String) param.args[8];
                    String threadKeysString = (String) param.args[9];
                    String typesString = (String) param.args[10];
                    String replyMessageId = (String) param.args[11];
                    TextMessage originalMessage = new TextMessage.Builder(message)
                            .setMentions(Mention.fromDispatchArgs(message, rangeStartsString, rangeEndsString, threadKeysString, typesString))
                            .setReplyMessageId(replyMessageId)
                            .build();

                    notifyListenersWithResult((listener) -> ((MessageSentListener) listener).onMessageSent(originalMessage, threadKey));

                    if (getListenersReturnValue().isConsumed && getListenersReturnValue().value == null) {
                        param.setResult(null);
                        return;
                    }

                    TextMessage refinedMessage = (TextMessage) getListenersReturnValue().value;
                    if (refinedMessage == null) return;

                    Logger.logObjectRecursive(refinedMessage);
                    param.args[5] = refinedMessage.content;
                    param.args[7] = Mention.joinRangeStarts(refinedMessage.mentions);
                    param.args[8] = Mention.joinRangeEnds(refinedMessage.mentions);
                    param.args[9] = Mention.joinThreadKeys(refinedMessage.mentions);
                    param.args[10] = Mention.joinTypes(refinedMessage.mentions);
                    param.args[11] = refinedMessage.replyMessageId;
                }
            }
        }));
    }

    public interface MessageSentListener {
        HookListenerResult<TextMessage> onMessageSent(TextMessage message, Long threadKey);
    }
}
