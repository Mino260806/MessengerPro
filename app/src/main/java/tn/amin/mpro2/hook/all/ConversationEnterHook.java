package tn.amin.mpro2.hook.all;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.helper.OrcaHookHelper;
import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;

public class ConversationEnterHook extends BaseHook {
    @Override
    public HookId getId() {
        return HookId.CONVERSATION_ENTER;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        return OrcaHookHelper.hookFeature(gateway.unobfuscator.getAPICode(OrcaUnobfuscator.API_CONVERSATION_ENTER),
                "O", "Orca", gateway.classLoader, wrap(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        long threadKey = Long.parseLong((String) param.args[6]);
                        notifyListeners((listener) -> ((ConversationEnterListener) listener).onConversationEnter(threadKey));
                    }
                }));
    }

    public interface ConversationEnterListener {
        void onConversationEnter(Long threadKey);
    }
}
