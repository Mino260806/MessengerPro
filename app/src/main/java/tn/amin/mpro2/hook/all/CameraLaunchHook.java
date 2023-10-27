package tn.amin.mpro2.hook.all;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.HookTime;
import tn.amin.mpro2.hook.helper.FragmentHookHelper;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;

public class CameraLaunchHook extends BaseHook {
    private static final int CONVERSATION_CAMERA_REQUEST_CODE = 7377;

    @Override
    public HookId getId() {
        return HookId.CAMERA_LAUNCH;
    }

    @Override
    public HookTime getHookTime() {
        return HookTime.UI;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        return FragmentHookHelper.interceptStartActivityForResult(
                CONVERSATION_CAMERA_REQUEST_CODE, wrap((intent) -> {
                    notifyListenersWithResult((listener) -> ((CameraLaunchListener) listener).onCameraLaunch());
                    return getListenersReturnValue().isConsumed && (Boolean) getListenersReturnValue().value;
                }), gateway.classLoader, this::wrapIgnoreWorking);
    }

    public interface CameraLaunchListener {
        HookListenerResult<Boolean> onCameraLaunch();
    }
}
