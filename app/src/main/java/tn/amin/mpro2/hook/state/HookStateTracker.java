package tn.amin.mpro2.hook.state;

abstract public class HookStateTracker {
    abstract public void updateState(HookState state);
    abstract public HookState getState();
    abstract public int getStateValue();

    public final boolean shouldNotApply() {
        return getStateValue() < 0;
    }
}
