package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Dao;
import androidx.room.Transaction;

import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.ContentSizeTuple;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Dao
public abstract class BaseCategoryFolderDao<T extends Category, U extends BaseTuple> extends BaseDao<T, U> {
    @Transaction
    public LiveData<List<T>> getAllModelsLiveWithContentSize() {
        LiveData<List<T>> allModelsLive = getAllModelsLive();
        LiveData<List<ContentSizeTuple>> folderContentSizeTuplesLive = getFolderContentSizeTuplesLive();
        LiveData<List<ContentSizeTuple>> sizeContentSizeTuplesLive = getSizeContentSizeTuplesLive();
        MediatorLiveData<List<T>> mediatorLive = new MediatorLiveData<>();

        mediatorLive.addSource(allModelsLive, models -> {
            setFolderContentSize(models, folderContentSizeTuplesLive.getValue());
            setSizeContentSize(models, sizeContentSizeTuplesLive.getValue());
            mediatorLive.setValue(models);
        });

        mediatorLive.addSource(folderContentSizeTuplesLive, folderContentSizeTuples -> {
            setFolderContentSize(allModelsLive.getValue(), folderContentSizeTuples);
            setSizeContentSize(allModelsLive.getValue(), sizeContentSizeTuplesLive.getValue());
            mediatorLive.setValue(allModelsLive.getValue());
        });
        mediatorLive.addSource(sizeContentSizeTuplesLive, sizeContentSizeTuples -> {
            setFolderContentSize(allModelsLive.getValue(), folderContentSizeTuplesLive.getValue());
            setSizeContentSize(allModelsLive.getValue(), sizeContentSizeTuplesLive.getValue());
            mediatorLive.setValue(allModelsLive.getValue());
        });
        return mediatorLive;
    }

    protected abstract LiveData<List<T>> getAllModelsLive();

    protected abstract LiveData<List<ContentSizeTuple>> getFolderContentSizeTuplesLive();

    protected abstract LiveData<List<ContentSizeTuple>> getSizeContentSizeTuplesLive();

    private void setFolderContentSize(List<T> models, List<ContentSizeTuple> folderContentSizeTuples) {
        if (models != null)
            models.forEach(model ->
                    getContentSizeOptional(folderContentSizeTuples, model)
                            .ifPresent(folderContentSizeTuple -> model.setFolderContentSize(folderContentSizeTuple.getSize())));
    }

    private void setSizeContentSize(List<T> models, List<ContentSizeTuple> sizeContentSizeTuples) {
        if (models != null)
            models.forEach(model ->
                    getContentSizeOptional(sizeContentSizeTuples, model)
                            .ifPresent(sizeContentSizeTuple -> model.setSizeContentSize(sizeContentSizeTuple.getSize())));
    }

    @NotNull
    private Optional<ContentSizeTuple> getContentSizeOptional(List<ContentSizeTuple> contentSizeTuples, T model) {
        return getContentSizeTuplesStream(contentSizeTuples)
                .filter(contentSizeTuple ->
                        contentSizeTuple.getSize() != 0
                                && contentSizeTuple.getParentId() == model.getId())
                .findAny();
    }

    @NotNull
    private Stream<ContentSizeTuple> getContentSizeTuplesStream(List<ContentSizeTuple> contentSizeTuples) {
        return contentSizeTuples != null ? contentSizeTuples.stream() : Stream.empty();
    }
}
