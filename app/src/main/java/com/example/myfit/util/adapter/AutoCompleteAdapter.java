package com.example.myfit.util.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfit.util.ktw.KoreanTextMatcher;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> mBindList = new ArrayList<>();
    private List<String> mOriginList;

    public AutoCompleteAdapter(@NonNull @NotNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public void setItem(List<String> autoCompleteList) {
        clear();
        this.mOriginList = autoCompleteList;
        notifyDataSetChanged();
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

    private class AutoCompleteAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");

            mBindList = mOriginList.stream().filter(word -> {
                String word2 = word.toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
                return KoreanTextMatcher.isMatch(word, keyWord);
            }).collect(Collectors.toList());
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
