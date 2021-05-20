package com.example.myfit.ui.dialog.eidttext.add.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCategoryViewModel extends BaseAddViewModel {
    private final CategoryRepository categoryRepository;
    private byte parentIndex;
    private String name;

    @Inject
    public AddCategoryViewModel(SavedStateHandle savedStateHandle, CategoryRepository categoryRepository) {
        super(savedStateHandle);
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected LiveData<Boolean> query(String name) {
        return categoryRepository.isExistingName((String) name, this.parentIndex);
    }

    public void setStateHandle(String name, byte parentIndex) {
        this.name = name;
        this.parentIndex = parentIndex;
        setStateHandle(name);
    }

    @Override
    public void insert() {
        categoryRepository.insert(name, parentIndex);
    }
}
