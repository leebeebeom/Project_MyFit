package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.util.constant.Sort;

import java.util.List;

import static com.example.myfit.di.SharedPreferencesModule.SORT;

public abstract class BaseRepository<T extends BaseTuple> {
    public abstract void deleteOrRestore(long[] ids);

    public void changeSort(Sort sort) {
        if (sort != getSort())
            getPreference().edit().putInt(SORT, sort.getValue()).apply();
    }

    public Sort getSort() {
        int sort = getPreference().getInt(SORT, Sort.SORT_CUSTOM.getValue());
        return Sort.values()[sort];
    }

    protected abstract SharedPreferences getPreference();

    public abstract void updateTuples(List<T> itemList);
}
