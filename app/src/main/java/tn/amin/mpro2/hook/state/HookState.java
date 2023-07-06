package tn.amin.mpro2.hook.state;

public enum HookState {
    DISABLED(-2),
    NOT_WORKING(-1),
    PENDING(0),
    APPLIED(1),
    WORKING(2);

    private final int mValue;
    HookState(int value) {
        mValue = value;
    }

    public static HookState fromValue(int value) {
        for (HookState state: HookState.values()) {
            if (state.getValue() == value)
                return state;
        }
        return null;
    }

    public int getValue() {
        return mValue;
    }
}
