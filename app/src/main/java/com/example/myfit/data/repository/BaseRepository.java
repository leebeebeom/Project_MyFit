package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import com.example.myfit.util.constant.Sort;

public abstract class BaseRepository {
    public abstract void deleteOrRestore(long[] ids, boolean isDeleted);

    public void changeSort(Sort sort) {
        if (sort.getValue() != getPreference().getInt(getPreferenceKey(), Sort.SORT_CUSTOM.getValue())) {
            SharedPreferences.Editor edit = getPreference().edit();
            edit.putInt(getPreferenceKey(), sort.getValue());
            edit.apply();
        }
    }

    public Sort getSort() {
        int sort = getPreference().getInt(getPreferenceKey(), Sort.SORT_CUSTOM.getValue());
        return Sort.values()[sort];
    }

    protected abstract SharedPreferences getPreference();

    protected abstract String getPreferenceKey();
}
