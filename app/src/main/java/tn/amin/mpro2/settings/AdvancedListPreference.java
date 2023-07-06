package tn.amin.mpro2.settings;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.google.gson.Gson;

import tn.amin.mpro2.R;
import tn.amin.mpro2.features.util.message.formatting.FormattingEntryData;

public class AdvancedListPreference extends Preference {
    FormattingEntryData[] entryDatas = new FormattingEntryData[] {};
    private ListPreferenceAdapter mAdapter;

    public AdvancedListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AdvancedListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AdvancedListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedListPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(getTitle())
                    .setView(getDialogView())
                    .show();
            return true;
        });

        String persisted = getPersistedString(
                new Gson().toJson(getDefaultValue())
        );

        entryDatas = new Gson().fromJson(persisted, FormattingEntryData[].class);
    }

    private View getDialogView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_formatting, null, false);

        ListView listView = dialogView.findViewById(R.id.listview);
        Button buttonAdd = dialogView.findViewById(R.id.button_add);

        mAdapter = new ListPreferenceAdapter();
        listView.setAdapter(mAdapter);

        buttonAdd.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
//            ((Activity) getContext()).startActivityForResult(intent, );
            throw new UnsupportedOperationException();
        });

        return dialogView;
    }

    private Object getDefaultValue() {
        return new FormattingEntryData[] {
                new FormattingEntryData('#', "upsidedowntext")
        };
    }

    private class ListPreferenceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return entryDatas.length;
        }

        @Override
        public Object getItem(int i) {
            return entryDatas[i].delimiter + " " + entryDatas[i].fileName;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.row_formatting, viewGroup, false);
            }

            TextView textView = view.findViewById(R.id.text);
            textView.setText(getItem(i).toString());

            return view;
        }
    }
}
