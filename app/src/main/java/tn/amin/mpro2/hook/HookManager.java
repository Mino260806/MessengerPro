package tn.amin.mpro2.hook;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import tn.amin.mpro2.hook.state.HookState;
import tn.amin.mpro2.hook.state.HookStateTracker;
import tn.amin.mpro2.orca.OrcaGateway;

public class HookManager {
    private final SharedPreferences mHookStatePref;

    public HookManager(SharedPreferences hookStatePref) {
        mHookStatePref = hookStatePref;
    }

    private final Map<BaseHook, HookStateTracker> mHookStateTrackers = new LinkedHashMap<>();
    private final Map<HookId, BaseHook> mHooks = new HashMap<>();

    public void addHook(BaseHook hook) {
        hook.setStateTracker(addStateTracker(hook));
        mHooks.put(hook.getId(), hook);
    }

    public BaseHook getHook(HookId id) {
        return mHooks.get(id);
    }

    public void inject(OrcaGateway gateway, Predicate<BaseHook> filter) {
        for (BaseHook hook: mHooks.values()) {
            if (filter.test(hook)) {
                hook.inject(gateway);
            }
        }
    }

    private HookStateTracker addStateTracker(BaseHook hook) {
        HookStateTracker tracker = new PreferencesHookStateTracker(mHookStatePref, hook.getId().name());
        mHookStateTrackers.put(hook, tracker);
        return tracker;
    }

    @SuppressWarnings("unchecked")
    public void registerListener(HookId hookId, Object hookListener) {
        BaseHook hook = mHooks.get(hookId);
        if (hook == null) throw new RuntimeException("Non-existent hook " + hookId.name());

        hook.addListener(hookListener);
    }

    public SharedPreferences getStatePref() {
        return mHookStatePref;
    }

    public void reloadPending(OrcaGateway gateway) {
        for (Map.Entry<BaseHook, HookStateTracker> entries: mHookStateTrackers.entrySet()) {
            if (entries.getValue().getState().equals(HookState.PENDING)) {
                entries.getKey().inject(gateway);
            }
        }
    }


    public void resetStates() {
        for (HookStateTracker stateTracker : mHookStateTrackers.values()) {
            stateTracker.updateState(HookState.PENDING);
        }
    }

    private static class PreferencesHookStateTracker extends HookStateTracker {
        private final SharedPreferences mSharedPreferences;
        private final String mKey;

        public PreferencesHookStateTracker(SharedPreferences sharedPreferences, String key) {
            mSharedPreferences = sharedPreferences;
            mKey = key;
        }

        @Override
        public void updateState(HookState state) {
            mSharedPreferences.edit()
                    .putInt("HOOK/" + mKey, state.getValue())
                    .apply();
        }

        @Override
        public HookState getState() {
            return HookState.fromValue(getStateValue());
        }

        @Override
        public int getStateValue() {
            return mSharedPreferences.getInt("HOOK/" + mKey, HookState.PENDING.getValue());
        }
    }
}
