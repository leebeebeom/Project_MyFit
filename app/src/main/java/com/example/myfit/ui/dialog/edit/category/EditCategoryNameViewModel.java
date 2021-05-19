package com.example.myfit.ui.dialog.edit.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.edit.BaseEditViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditCategoryNameViewModel extends BaseEditViewModel {
    public static final String EDIT_CATEGORY_NAME = "edit category name";
    SavedStateHandle savedStateHandle;
    CategoryRepository categoryRepository;
    private byte parentIndex;
    private String name;
    private long id;

    @Inject
    public EditCategoryNameViewModel(@NotNull SavedStateHandle savedStateHandle, CategoryRepository categoryRepository) {
        this.savedStateHandle = savedStateHandle;
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected LiveData<Boolean> getIsExistingLive() {
        return Transformations.switchMap(savedStateHandle.getLiveData(EDIT_CATEGORY_NAME),
                name -> categoryRepository.isExistingName((String) name, parentIndex));
    }

    @Override
    public void queryIsExistingName(long id, String inputText, byte parentIndex) {
        this.id = id;
        this.name = inputText;
        this.parentIndex = parentIndex;
        savedStateHandle.set(EDIT_CATEGORY_NAME, name);
    }

    @Override
    public void queryIsExistingName(long id, String inputText, long parentId) {

    }

    @Override
    public void update() {
        categoryRepository.update(id, name);
    }
}
