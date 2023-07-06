package tn.amin.mpro2.settings.hookstate;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;

import java.util.ArrayList;
import java.util.Map;

import tn.amin.mpro2.R;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.state.HookState;

public class HookStateAdapter extends BaseAdapter {
    private final SharedPreferences mSharedPreferences;
    private final ArrayList<HookStateModel> mHookStateList = new ArrayList<>();

    public HookStateAdapter(SharedPreferences hookStateSharedPreferences) {
        mSharedPreferences = hookStateSharedPreferences;

        for (Map.Entry<String, ?> hookStateEntry: hookStateSharedPreferences.getAll().entrySet()) {
            if (hookStateEntry.getKey().startsWith("HOOK/")) {
                int value = (int) hookStateEntry.getValue();
                String name = hookStateEntry.getKey().split("/")[1];
                mHookStateList.add(new HookStateModel(name, HookState.fromValue(value)));
            }
        }
    }

    @Override
    public int getCount() {
        return mHookStateList.size();
    }

    @Override
    public HookStateModel getItem(int i) {
        return mHookStateList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mHookStateList.get(i).key.hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HookStateModel model = getItem(i);
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_hookstate, viewGroup, false);
        }

        TextView nameView = view.findViewById(R.id.textview_name);
        nameView.setText(model.key);

        TextView stateView = view.findViewById(R.id.textview_state);
        stateView.setText(model.state.toString());

        CheckBox checkBox = view.findViewById(R.id.checkbox_hook);
        checkBox.setChecked(model.state.getValue() > -2);
        checkBox.setOnCheckedChangeListener((b, checked) -> {
            updateState(model, checked? HookState.PENDING: HookState.DISABLED);
            stateView.setText(model.state.toString());
        });

        ImageButton resetButton = view.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(v -> {
            updateState(model, HookState.PENDING);
            notifyDataSetChanged();
        });

        return view;
    }

    private void updateState(HookStateModel model, HookState state) {
        mSharedPreferences.edit()
                .putInt("HOOK/" + model.key, state.getValue())
                .apply();
        model.state = state;
    }
}
