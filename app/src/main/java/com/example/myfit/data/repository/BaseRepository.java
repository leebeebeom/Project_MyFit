package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.repository.dao.BaseDao;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.util.SortUtil;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
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
    protected Stream<U> getUndeletedSortedStreamByParentIndex(List<U> models, int parentIndex) {
        return SortUtil.sortCategoryFolderStream(getSort(), getUndeletedStreamByParentIndex(models, parentIndex));
    }

    protected Stream<U> getUndeletedSortedStream(List<U> models) {
        return SortUtil.sortCategoryFolderStream(getSort(), getUnDeletedStream(models));
    }

    protected Stream<U> getUndeletedStreamByParentIndex(List<U> models, int parentIndex) {
        return getUnDeletedStream(models).filter(model -> model.getParentIndex() == parentIndex);
    }

    protected Stream<Folder> getFolderStreamByPrentId(Stream<Folder> stream, long parentId) {
        return stream.filter(folder -> folder.getParentId() == parentId);
    }

    protected Stream<U> getUnDeletedSearchStream(List<U> models) {
        return getUnDeletedStream(models).sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    @NotNull
    protected Stream<U> getUnDeletedStream(List<U> models) {
        return getStream(models).filter(model -> !model.isDeleted());
    }

    @NotNull
    protected Stream<U> getDeletedStream(List<U> models) {
        return getStream(models).filter(BaseModel::isDeleted);
    }

    protected Stream<U> getDeletedSortedStream(List<U> models) {
        return getDeletedStream(models).sorted((o1, o2) -> Long.compare(o1.getDeletedTime(), o2.getDeletedTime()));
    }

    protected Stream<U> getDeletedSearchStream(List<U> models) {
        return getDeletedStream(models).sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    protected Stream<U> getStreamByIds(List<U> models, long[] ids) {
        return getStream(models).filter(model -> Arrays.stream(ids).anyMatch(id -> model.getId() == id));
    }

    @NotNull
    private Stream<U> getStream(List<U> models) {
        if (models != null)
            return models.stream();
        else return Stream.empty();
    }

    protected <T extends BaseTuple> List<List<T>> getClassifiedTuplesByParentIndex(List<T> tuples) {
        Stream<T> stream = tuples.stream();
        List<T> topList = stream.filter(tuple -> tuple.getParentIndex() == 0).collect(Collectors.toList());
        List<T> bottomList = stream.filter(tuple -> tuple.getParentIndex() == 1).collect(Collectors.toList());
        List<T> outerList = stream.filter(tuple -> tuple.getParentIndex() == 2).collect(Collectors.toList());
        List<T> etcList = stream.filter(tuple -> tuple.getParentIndex() == 3).collect(Collectors.toList());
        return Arrays.asList(topList, bottomList, outerList, etcList);
    }

    protected Optional<U> getSingleOptional(List<U> models, long id) {
        return getStream(models).filter(model -> model.getId() == id).findAny();
    }

    protected void setDeleted(Stream<U> stream) {
        AtomicLong currentTime = new AtomicLong(getCurrentTime());
        stream.forEach(model -> {
            model.setDeleted(!model.isDeleted());
            model.setDeletedTime(model.isDeleted() ? currentTime.incrementAndGet() : 0);
        });
    }

    protected Integer getLargestSortNumber(List<U> models) {
        List<Integer> sortNumbers = getStream(models).map(BaseModel::getSortNumber).collect(Collectors.toList());
        return Collections.max(sortNumbers);
    }

    protected boolean isExistingName(List<U> models, int parentIndex, String name) {
        Stream<U> undeletedStreamByParentIndex = getUndeletedStreamByParentIndex(models, parentIndex);
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
}
