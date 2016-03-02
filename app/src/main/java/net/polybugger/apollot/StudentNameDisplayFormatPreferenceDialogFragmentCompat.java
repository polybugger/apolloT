package net.polybugger.apollot;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import net.polybugger.apollot.db.StudentDbAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentNameDisplayFormatPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    StudentNameDisplayFormatDialogPreference mPreference;
    private ListView mListView;
    private StudentDbAdapter.NameDisplayFormat mPersistedFormat;
    private ArrayList<StudentDbAdapter.NameDisplayFormat> mListData;
    private ListArrayAdapter mListAdapter;

    @Override
    public void onDialogClosed(boolean b) {

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setTitle(null).setPositiveButton(null, null).setNegativeButton(null, null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mPreference = (StudentNameDisplayFormatDialogPreference) getPreference();

        mPersistedFormat = StudentDbAdapter.NameDisplayFormat.fromInteger(mPreference.getPersistedInt(StudentDbAdapter.NameDisplayFormat.LAST_NAME_FIRST_NAME.getValue()));
        ((TextView) view.findViewById(R.id.title_text_view)).setText(mPreference.getTitle());

        mListData = new ArrayList<StudentDbAdapter.NameDisplayFormat>();
        mListData.add(StudentDbAdapter.NameDisplayFormat.LAST_NAME_FIRST_NAME);
        mListData.add(StudentDbAdapter.NameDisplayFormat.FIRST_NAME_LAST_NAME);

        mListView = (ListView) view.findViewById(R.id.list_view);
        mListAdapter = new ListArrayAdapter(mPreference.getContext(), mListData);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text_view);
                StudentDbAdapter.NameDisplayFormat nameDisplayFormat = (StudentDbAdapter.NameDisplayFormat) textView.getTag();
                mPersistedFormat = nameDisplayFormat;
                mListAdapter.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.set_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreference.persistInt(mPersistedFormat.getValue());
                StudentDbAdapter.DISPLAY_FORMAT = mPersistedFormat;
                StudentDbAdapter.ITEM_RECORDS_CHANGED_CALLBACK = true;
                StudentDbAdapter.CLASS_STUDENTS_CHANGED_CALLBACK = true;
                dismiss();
            }
        });
    }

    private class ListArrayAdapter extends ArrayAdapter<StudentDbAdapter.NameDisplayFormat> {

        private class ViewHolder {
            RadioButton imageRadio;
            TextView textView;
        }

        public ListArrayAdapter(Context context, List<StudentDbAdapter.NameDisplayFormat> objects) {
            super(context, R.layout.pref_student_name_display_format_row, R.id.text_view, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StudentDbAdapter.NameDisplayFormat displayFormat = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mPreference.getContext()).inflate(R.layout.pref_student_name_display_format_row, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
                viewHolder.imageRadio = (RadioButton) convertView.findViewById(R.id.text_radio);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            switch(displayFormat) {
                case LAST_NAME_FIRST_NAME:
                    viewHolder.textView.setText(mPreference.getContext().getString(R.string.pref_last_name_first_name));
                    break;
                case FIRST_NAME_LAST_NAME:
                    viewHolder.textView.setText(mPreference.getContext().getString(R.string.pref_first_name_last_name));
                    break;
            }
            viewHolder.textView.setTag(displayFormat);
            viewHolder.imageRadio.setChecked(mPersistedFormat == displayFormat);
            return convertView;
        }
    }

    public static StudentNameDisplayFormatPreferenceDialogFragmentCompat newInstance(Preference preference) {
        StudentNameDisplayFormatPreferenceDialogFragmentCompat fragment = new StudentNameDisplayFormatPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }
}
