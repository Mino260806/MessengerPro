package tn.amin.mpro2.settings.hookstate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tn.amin.mpro2.R;

public class HookStateFragment extends Fragment {
    private final SharedPreferences mSharedPreferences;

    public HookStateFragment(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup content = (ViewGroup) inflater.inflate(R.layout.fragment_hookstate, container, false);

        ListView listView = content.findViewById(R.id.listview_hookstate);
        listView.setAdapter(new HookStateAdapter(mSharedPreferences));

        return content;
    }
}
