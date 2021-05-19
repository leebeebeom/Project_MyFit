package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import com.example.myfit.util.constant.SortValue;

public abstract class BaseRepository {
    public abstract void deleteOrRestore(long[] ids, boolean isDeleted);

    public void changeSort(int sort) {
        if (sort != getPreference().getInt(getPreferenceKey(), SortValue.SORT_CUSTOM.getValue())) {
            SharedPreferences.Editor edit = getPreference().edit();
            edit.putInt(getPreferenceKey(), sort);
            edit.apply();
        }
    }

    public int getSort() {
        return getPreference().getInt(getPreferenceKey(), SortValue.SORT_CUSTOM.getValue());
    }

    protected abstract SharedPreferences getPreference();

    protected abstract String getPreferenceKey();
}
