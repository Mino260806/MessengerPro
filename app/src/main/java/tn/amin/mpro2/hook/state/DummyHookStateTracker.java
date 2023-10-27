package tn.amin.mpro2.hook.state;

public class DummyHookStateTracker extends HookStateTracker {
    public final static DummyHookStateTracker INSTANCE = new DummyHookStateTracker();

    @Override
    public void updateState(HookState state) {
    }

    @Override
    public HookState getState() {
        return HookState.WORKING;
    }

    @Override
    public int getStateValue() {
        return HookState.WORKING.getValue();
    }
}
