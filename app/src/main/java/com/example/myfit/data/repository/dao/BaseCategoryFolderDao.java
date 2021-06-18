package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Dao;
import androidx.room.Transaction;

import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.ContentSizeTuple;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public abstract class BaseCategoryFolderDao<T extends Category, U extends BaseTuple> extends BaseDao<T, U> {
    @Transaction
    public LiveData<List<T>> getAllModelsLiveWithContentSize() {
        LiveData<List<T>> allModelsLive = getAllModelsLive();
        LiveData<List<ContentSizeTuple>> folderContentSizeTuplesLive = getFolderContentSizeTuplesLive();
        LiveData<List<ContentSizeTuple>> sizeContentSizeTuplesLive = getSizeContentSizeTuplesLive();
        MediatorLiveData<List<T>> mediatorLive = new MediatorLiveData<>();

        mediatorLive.addSource(allModelsLive, models -> {
            setFolderContentSize(models, getContentSizeTuplesLiveValue(folderContentSizeTuplesLive));
            setSizeContentSize(models, getContentSizeTuplesLiveValue(sizeContentSizeTuplesLive));
            mediatorLive.setValue(models);
        });

        mediatorLive.addSource(folderContentSizeTuplesLive, folderContentSizeTuples -> {
            setFolderContentSize(getAllModelsLiveValue(allModelsLive), folderContentSizeTuples);
            setSizeContentSize(getAllModelsLiveValue(allModelsLive), getContentSizeTuplesLiveValue(sizeContentSizeTuplesLive));
            mediatorLive.setValue(getAllModelsLiveValue(allModelsLive));
        });
        mediatorLive.addSource(sizeContentSizeTuplesLive, sizeContentSizeTuples -> {
            setFolderContentSize(getAllModelsLiveValue(allModelsLive), getContentSizeTuplesLiveValue(folderContentSizeTuplesLive));
            setSizeContentSize(getAllModelsLiveValue(allModelsLive), getContentSizeTuplesLiveValue(sizeContentSizeTuplesLive));
            mediatorLive.setValue(getAllModelsLiveValue(allModelsLive));
        });
        return mediatorLive;
    }

    protected abstract LiveData<List<T>> getAllModelsLive();

    private List<T> getAllModelsLiveValue(LiveData<List<T>> allModelsLive) {
        return Optional.ofNullable(allModelsLive.getValue()).orElse(new ArrayList<>());
    }

    protected abstract LiveData<List<ContentSizeTuple>> getFolderContentSizeTuplesLive();

    protected abstract LiveData<List<ContentSizeTuple>> getSizeContentSizeTuplesLive();

    private List<ContentSizeTuple> getContentSizeTuplesLiveValue(LiveData<List<ContentSizeTuple>> contentSizeTuplesLive) {
        return Optional.ofNullable(contentSizeTuplesLive.getValue()).orElse(new ArrayList<>());
    }

    private void setFolderContentSize(List<T> models, List<ContentSizeTuple> folderContentSizeTuples) {
        models.forEach(model ->
                getContentSizeOptional(folderContentSizeTuples, model)
                        .ifPresent(folderContentSizeTuple -> model.setFolderContentSize(folderContentSizeTuple.getSize())));
    }

    private void setSizeContentSize(List<T> models, List<ContentSizeTuple> sizeContentSizeTuples) {
        models.forEach(model ->
                getContentSizeOptional(sizeContentSizeTuples, model)
                        .ifPresent(sizeContentSizeTuple -> model.setSizeContentSize(sizeContentSizeTuple.getSize())));
    }

    @NotNull
    private Optional<ContentSizeTuple> getContentSizeOptional(List<ContentSizeTuple> contentSizeTuples, T model) {
        return contentSizeTuples.stream()
                .filter(contentSizeTuple ->
                        contentSizeTuple.getSize() != 0 && contentSizeTuple.getParentId() == model.getId())
                .findAny();
    }
}
