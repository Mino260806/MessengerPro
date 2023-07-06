package tn.amin.mpro2.ui.toolbar;

import tn.amin.mpro2.preference.ModulePreferences;
import tn.amin.mpro2.ui.touch.SwipeDirection;

public class MProToolbarSummonPropertiesProvider implements MProToolbar.SummonPropertiesProvider {
    private final ModulePreferences mPref;

    public MProToolbarSummonPropertiesProvider(ModulePreferences pref) {
        mPref = pref;
    }

    @Override
    public int getFingersCount() {
        return mPref.getToolbarSummonFingersCount();
    }

    @Override
    public SwipeDirection getSwipeDirection() {
        return mPref.getToolbarSummonSwipeDirection();
    }

    @Override
    public boolean getFromEdge() {
        return mPref.getToolbarSummonFromEdge();
    }
}
