package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;

import java.util.List;

public abstract class BaseRepository<T extends BaseTuple> {
    public abstract void deleteOrRestore(long[] ids);

    public void changeSort(Sort sort) {
        if (sort != getSort())
            getSortPreference().edit().putInt(SharedPreferenceKey.SORT.getValue(), sort.getValue()).apply();
    }

    public Sort getSort() {
        int sort = getSortPreference().getInt(SharedPreferenceKey.SORT.getValue(), Sort.SORT_CUSTOM.getValue());
        return Sort.values()[sort];
    }

    protected abstract SharedPreferences getSortPreference();

    public abstract void updateTuples(List<T> itemList);
}
