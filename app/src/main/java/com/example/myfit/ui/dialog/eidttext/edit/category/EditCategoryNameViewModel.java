package com.example.myfit.ui.dialog.eidttext.edit.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditCategoryNameViewModel extends BaseEditViewModel {
    private final CategoryRepository categoryRepository;
    private int parentIndex;
    private String name;
    private long id;

    @Inject
    public EditCategoryNameViewModel(SavedStateHandle savedStateHandle, CategoryRepository categoryRepository) {
        super(savedStateHandle);
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected LiveData<Boolean> queryIsExistingLive(String name) {
        return categoryRepository.isExistingName((String) name, parentIndex);
    }

    public void setStateHandle(long id, String inputText, int parentIndex) {
        this.id = id;
        this.name = inputText;
        this.parentIndex = parentIndex;
        setStateHandle(name);
    }

    @Override
    public void update() {
        categoryRepository.update(id, name);
    }
}
