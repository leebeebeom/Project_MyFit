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

    private class StringFilter extends Filter {
        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase().trim();

            ArrayList<String> filteredList = new ArrayList<>();
            for (String s : mOriginList)
                if (s.toLowerCase().contains(filterString))
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
        return new StringFilter();
    }
}
