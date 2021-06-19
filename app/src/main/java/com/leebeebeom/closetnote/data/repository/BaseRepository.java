package com.leebeebeom.closetnote.data.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.leebeebeom.closetnote.data.model.BaseModel;
import com.leebeebeom.closetnote.data.model.model.Folder;
import com.leebeebeom.closetnote.data.repository.dao.BaseDao;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.util.SortUtil;
import com.leebeebeom.closetnote.util.constant.ParentCategory;
import com.leebeebeom.closetnote.util.constant.SharedPreferenceKey;
import com.leebeebeom.closetnote.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseRepository<U extends BaseModel, V extends BaseTuple> {
    protected <T extends BaseTuple> List<List<T>> getClassifiedTuplesByParentIndex(List<T> tuples) {
        List<T> topList = new LinkedList<>();
        List<T> bottomList = new LinkedList<>();
        List<T> outerList = new LinkedList<>();
        List<T> etcList = new LinkedList<>();
        tuples.forEach(tuple -> {
            if (tuple.getParentIndex() == ParentCategory.TOP.getValue())
                topList.add(tuple);
            else if (tuple.getParentIndex() == ParentCategory.BOTTOM.getValue())
                bottomList.add(tuple);
            else if (tuple.getParentIndex() == ParentCategory.OUTER.getValue())
                outerList.add(tuple);
            else etcList.add(tuple);
        });
        return Arrays.asList(topList, bottomList, outerList, etcList);
    }

    protected Stream<U> getUndeletedSortedStream(List<U> models) {
        return SortUtil.sortCategoryFolderStream(getSort(), getUnDeletedStream(models));
    }

    protected Optional<String> getNameOptional(List<U> models, long id) {
        return getSingleOptional(models, id).map(BaseModel::getName);
    }

    protected Stream<U> getStreamById(List<U> models, long id) {
        return getStream(models).filter(model -> model.getId() == id);
    }

    protected Stream<U> getUndeletedSortedStreamByParentIndex(List<U> models, int parentIndex) {
        return SortUtil.sortCategoryFolderStream(getSort(), getUndeletedStreamByParentIndex(models, parentIndex));
    }

    protected boolean isExistingName(List<U> models, int parentIndex, String name) {
        Stream<U> undeletedStreamByParentIndex = getUndeletedStreamByParentIndex(models, parentIndex);
        return undeletedStreamByParentIndex.anyMatch(model -> model.getName().equals(name.trim()));
    }

    protected Stream<U> getUndeletedStreamByParentIndex(List<U> models, int parentIndex) {
        return getUnDeletedStream(models).filter(model -> model.getParentIndex() == parentIndex);
    }

    @NotNull
    protected Stream<U> getUnDeletedStream(List<U> models) {
        return getStream(models).filter(model -> !model.isDeleted());
    }

    protected Optional<U> getSingleOptional(List<U> models, long id) {
        return getStream(models).filter(model -> model.getId() == id).findAny();
    }

    protected List<U> getModelsByIds(List<U> models, long[] ids) {
        return getStream(models)
                .filter(model -> Arrays.stream(ids).anyMatch(id -> model.getId() == id))
                .collect(Collectors.toList());
    }

    @NotNull
    private Stream<U> getStream(List<U> models) {
        return models.stream();
    }


    protected Stream<Folder> getFolderStreamByPrentId(Stream<Folder> stream, long parentId) {
        return stream.filter(folder -> folder.getParentId() == parentId);
    }

    protected Stream<U> getUnDeletedSearchStream(List<U> models) {
        return getUnDeletedStream(models).sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    @NotNull
    protected Stream<U> getDeletedStream(List<U> models) {
        return getStream(models).filter(BaseModel::isDeleted);
    }

    protected void setDeleted(List<U> models) {
        AtomicLong currentTime = new AtomicLong(getCurrentTime());
        models.forEach(model -> {
            model.setDeleted(!model.isDeleted());
            model.setDeletedTime(model.isDeleted() ? currentTime.incrementAndGet() : 0);
        });
    }

    protected boolean isExistingName(List<Folder> models, long parentId, String name) {
//        getFolderStreamByPrentId()
//        Stream<U> undeletedStreamByParentIndex = getUndeletedStreamByParentIndex(models, parentIndex);
//        return undeletedStreamByParentIndex.anyMatch(model -> model.getName().equals(name.trim()));
        //TODO
        return false;
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

    protected int getLargestSortNumber() {
        SharedPreferences largestSortPreference = getLargestSortNumberPreference();
        int largestSortNumber = largestSortPreference.getInt(SharedPreferenceKey.LARGEST_SORT_NUMBER.getValue(), 0);
        largestSortPreference.edit().putInt(SharedPreferenceKey.LARGEST_SORT_NUMBER.getValue(), largestSortNumber + 1).apply();
        return largestSortNumber;
    }

    protected abstract SharedPreferences getSortPreference();

    protected abstract SharedPreferences getLargestSortNumberPreference();

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

    public List<U> getAllModelsLiveValue() {
        return Optional.ofNullable(getModelsLive().getValue()).orElse(new ArrayList<>());
    }

    protected abstract LiveData<List<U>> getModelsLive();

    public void updateTuples(List<V> tuples) {
        getDao().updateTuples(tuples);
    }
}
