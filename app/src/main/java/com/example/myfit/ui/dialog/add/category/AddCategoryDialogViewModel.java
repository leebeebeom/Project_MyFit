package com.example.myfit.ui.dialog.add.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.BaseDialogViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCategoryDialogViewModel extends BaseDialogViewModel {
    public static final String CATEGORY_NAME = "category name";
    private final SavedStateHandle savedStateHandle;
    private final CategoryRepository categoryRepository;
    private byte parentIndex;
    private String name;

    @Inject
    public AddCategoryDialogViewModel(@NotNull SavedStateHandle savedStateHandle, CategoryRepository categoryRepository) {
        this.savedStateHandle = savedStateHandle;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void insert() {
        categoryRepository.insert(name, parentIndex);
    }

    @Override
    public void update() {
    }

    @Override
    protected LiveData<Boolean> getIsExistingLive() {
        return Transformations.switchMap(savedStateHandle.getLiveData(CATEGORY_NAME),
                name -> categoryRepository.isExistingName((String) name, this.parentIndex));
    }

    @Override
    public void queryIsExistingName(String name, byte parentIndex) {
        this.name = name;
        this.parentIndex = parentIndex;
        savedStateHandle.set(CATEGORY_NAME, name);
    }

    @Override
    public void queryIsExistingName(String inputText, long parentId, byte parentIndex) {
    }

    @Override
    public void queryIsExistingName(long id, String inputText, byte parentIndex) {

    }

    @Override
    public void queryIsExistingName(long id, String inputText, long parentId) {

    }
}
