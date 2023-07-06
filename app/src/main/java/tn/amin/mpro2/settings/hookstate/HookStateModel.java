package tn.amin.mpro2.settings.hookstate;

import tn.amin.mpro2.hook.state.HookState;

public class HookStateModel {
    String key;
    HookState state;

    public HookStateModel(String key, HookState state) {
        this.key = key;
        this.state = state;
    }
}
