package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseRepository {
    protected <T extends BaseTuple> List<List<T>> getClassifiedTuplesByParentIndex(List<T> tuples) {
        List<T> topList = tuples.stream().filter(tuple -> tuple.getParentIndex() == 0).collect(Collectors.toList());
        List<T> bottomList = tuples.stream().filter(tuple -> tuple.getParentIndex() == 1).collect(Collectors.toList());
        List<T> outerList = tuples.stream().filter(tuple -> tuple.getParentIndex() == 2).collect(Collectors.toList());
        List<T> etcList = tuples.stream().filter(tuple -> tuple.getParentIndex() == 3).collect(Collectors.toList());
        return Arrays.asList(topList, bottomList, outerList, etcList);
    }

    @NotNull
    protected <U extends BaseModel> Stream<U> getUnDeletedStream(List<U> models) {
        return models.stream().filter(model -> !model.isDeleted());
    }

    @NotNull
    protected <U extends BaseModel> Stream<U> getDeletedCategoryStream(List<U> models) {
        return models.stream().filter(BaseModel::isDeleted);
    }

    protected <U extends BaseModel> void setDeleted(List<U> models) {
        models.forEach(model -> model.setDeleted(!model.isDeleted()));
        setDeletedTime(models);
    }

    @Contract(pure = true)
    private <U extends BaseModel> void setDeletedTime(@NotNull List<U> deletedTuples) {
        AtomicLong currentTime = new AtomicLong(getCurrentTime());
        deletedTuples.forEach(deletedTuple -> deletedTuple.setDeletedTime(deletedTuple.isDeleted() ? currentTime.incrementAndGet() : 0));
    }

    protected long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }


    public void changeSort(Sort sort) {
        if (sort != getSort())
            getSortPreference().edit().putInt(SharedPreferenceKey.SORT.getValue(), sort.getValue()).apply();
    }

    public Sort getSort() {
        int sort = getSortPreference().getInt(SharedPreferenceKey.SORT.getValue(), Sort.SORT_CUSTOM.getValue());
        return Sort.values()[sort];
    }

    protected abstract SharedPreferences getSortPreference();
}
