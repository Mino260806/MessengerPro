package tn.amin.mpro2.hook.listener;

public class HookListenerResult<T> {
    public static <T> HookListenerResult<T> ignore() {
        return new HookListenerResult<>(false);
    }

    public static <T> HookListenerResult<T> consume() {
        return new HookListenerResult<>(true);
    }

    public static <T> HookListenerResult<T> consume(T returnValue) {
        return new HookListenerResult<>(true, returnValue);
    }

    public boolean isConsumed;
    public T value = null;

    public HookListenerResult(boolean isConsumed) {
        this.isConsumed = isConsumed;
    }

    public HookListenerResult(boolean isConsumed, T value) {
        this.isConsumed = isConsumed;
        this.value = value;
    }
}