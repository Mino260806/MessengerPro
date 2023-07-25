package tn.amin.mpro2.hook.all;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.HookTime;
import tn.amin.mpro2.hook.helper.OrcaHookHelper;
import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;

public class MessageReceivedHook extends BaseHook {
    @Override
    public HookId getId() {
        return HookId.MESSAGE_RECEIVE;
    }

    @Override
    public HookTime getHookTime() {
        return HookTime.AFTER_DEOBFUSCATION;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        return OrcaHookHelper.hookFeature(gateway.unobfuscator.getAPICode(OrcaUnobfuscator.API_NOTIFICATION),
                "VO", "Core", gateway.classLoader, wrap(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        long convThreadKey = (Long) param.args[2];
                        String messageId = (String) param.args[5];
                        String message = (String) param.args[7];
                        long senderUserKey = (Long) param.args[8];

                        notifyListeners((listener) ->
                                ((MessageReceivedListener) listener).onMessageReceived(message, messageId, senderUserKey, convThreadKey));
                    }
                }));

    }

    public interface MessageReceivedListener {
        void onMessageReceived(String message, String messageId, long senderUserKey, long convThreadKey);
    }
}
