package com.example.project_myfit.search.main.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project_myfit.util.ktw.KoreanTextMatcher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> mBindList;
    private List<String> mOriginList;

    public AutoCompleteAdapter(@NonNull @NotNull Context context, int resource, int textViewResourceId, @NonNull @NotNull List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.mBindList = objects;
        this.mOriginList = objects;
    }

    public AutoCompleteAdapter(@NonNull @NotNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public void setItem(List<String> autoCompleteList){
        clear();
        this.mBindList = autoCompleteList;
        this.mOriginList = autoCompleteList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mBindList != null) return mBindList.size();
        else return 0;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mBindList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class AutoCompleteAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
            ArrayList<String> filteredList = new ArrayList<>();
            for (String s : mOriginList)
                if (KoreanTextMatcher.isMatch(s.toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                    filteredList.add(s);

            mBindList = filteredList;
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new AutoCompleteAdapterFilter();
    }
}
