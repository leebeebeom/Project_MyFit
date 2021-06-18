package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.repository.dao.BaseDao;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.util.SortUtil;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseRepository<U extends BaseModel, V extends BaseTuple> {
    protected Stream<U> getUndeletedSortedStreamByParentIndex(int parentIndex) {
        return SortUtil.sortCategoryFolderStream(getSort(), getUndeletedStreamByParentIndex(parentIndex));
    }

    protected Stream<U> getUndeletedSortedStream() {
        return SortUtil.sortCategoryFolderStream(getSort(), getUnDeletedStream());
    }

    protected Stream<U> getUndeletedStreamByParentIndex(int parentIndex) {
        return getUnDeletedStream().filter(model -> model.getParentIndex() == parentIndex);
    }

    protected Stream<Folder> getFolderStreamByPrentId(Stream<Folder> stream, long parentId) {
        return stream.filter(folder -> folder.getParentId() == parentId);
    }

    protected Stream<U> getUnDeletedSearchStream() {
        return getUnDeletedStream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    @NotNull
    protected Stream<U> getUnDeletedStream() {
        return getStream().filter(model -> !model.isDeleted());
    }

    @NotNull
    protected Stream<U> getDeletedStream() {
        return getStream().filter(BaseModel::isDeleted);
    }

    protected Stream<U> getDeletedSortedStream() {
        return getDeletedStream().sorted((o1, o2) -> Long.compare(o1.getDeletedTime(), o2.getDeletedTime()));
    }

    protected Stream<U> getDeletedSearchStream() {
        return getDeletedStream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    protected Stream<U> getStreamByIds(long[] ids) {
        return getStream().filter(model -> Arrays.stream(ids).anyMatch(id -> model.getId() == id));
    }

    @NotNull
    private Stream<U> getStream() {
        return getModels().stream();
    }

    protected List<List<V>> getClassifiedTuplesByParentIndex(List<V> tuples) {
        Stream<V> stream = tuples.stream();
        List<V> topList = stream.filter(tuple -> tuple.getParentIndex() == 0).collect(Collectors.toList());
        List<V> bottomList = stream.filter(tuple -> tuple.getParentIndex() == 1).collect(Collectors.toList());
        List<V> outerList = stream.filter(tuple -> tuple.getParentIndex() == 2).collect(Collectors.toList());
        List<V> etcList = stream.filter(tuple -> tuple.getParentIndex() == 3).collect(Collectors.toList());
        return Arrays.asList(topList, bottomList, outerList, etcList);
    }

    protected Optional<U> getSingleOptional(long id) {
        return getStream().filter(model -> model.getId() == id).findAny();
    }

    protected Optional<U> getSingleOptionalByChildId(Folder folder) {
        return getStream().filter(model -> model.getId() == folder.getParentId()).findAny();
    }

    protected void setDeleted(Stream<U> stream) {
        AtomicLong currentTime = new AtomicLong(getCurrentTime());
        stream.forEach(model -> {
            model.setDeleted(!model.isDeleted());
            model.setDeletedTime(model.isDeleted() ? currentTime.incrementAndGet() : 0);
        });
    }

    protected Integer getLargestSortNumber() {
        List<Integer> sortNumbers = getStream().map(BaseModel::getSortNumber).collect(Collectors.toList());
        return Collections.max(sortNumbers);
    }

    protected boolean isExistingName(int parentIndex, String name) {
        Stream<U> undeletedStreamByParentIndex = getUndeletedStreamByParentIndex(parentIndex);
        return undeletedStreamByParentIndex.anyMatch(model -> model.getName().equals(name));
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

    public void insert(List<U> models) {
        new Thread(() -> getDao().insert(models)).start();
    }

    public void insert(U model) {
        new Thread(() -> getDao().insert(model)).start();
    }

    public void update(List<U> models) {
        new Thread(() -> getDao().update(models)).start();
    }

    public void update(U model) {
        new Thread(() -> getDao().update(model)).start();
    }

    protected abstract BaseDao<U, V> getDao();

    private List<U> getModels() {
        return Optional.ofNullable(getModelsLive().getValue()).orElse(new ArrayList<>());
    }

    protected abstract LiveData<List<U>> getModelsLive();
}
