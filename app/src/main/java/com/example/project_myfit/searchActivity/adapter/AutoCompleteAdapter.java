package com.example.project_myfit.searchActivity.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> mBindList;
    private final List<String> mOriginList;

    public AutoCompleteAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.mBindList = objects;
        this.mOriginList = objects;
    }

    @Override
    public int getCount() {
        return mBindList.size();
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

    private class StringFilter extends Filter {
        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase().trim();

            ArrayList<String> filteredList = new ArrayList<>();
            for (String s : mOriginList)
                if (s.toLowerCase().trim().contains(filterString))
                    filteredList.add(s.trim());
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
        return new StringFilter();
    }
}
