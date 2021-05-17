package com.example.myfit.ui.dialog.add.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.repository.CategoryRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCategoryDialogViewModel extends ViewModel {
    public static final String CATEGORY_NAME = "category name";
    private final CategoryRepository categoryRepository;
    private final SavedStateHandle savedStateHandle;
    private byte parentIndex;
    private String name;
    private final LiveData<Boolean> isExistingLive;

    @Inject
    public AddCategoryDialogViewModel(@NotNull SavedStateHandle savedStateHandle, CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.savedStateHandle = savedStateHandle;
        isExistingLive = Transformations.switchMap(savedStateHandle.getLiveData(CATEGORY_NAME),
                name -> categoryRepository.isExistingName((String) name, this.parentIndex));
    }

    public void queryIsExistingName(String name, byte parentIndex) {
        this.name = name;
        this.parentIndex = parentIndex;
        savedStateHandle.set(CATEGORY_NAME, name);
    }

    public void insert() {
        categoryRepository.insert(name, parentIndex);
    }

    public LiveData<Boolean> getIsExistingLive() {
        return isExistingLive;
    }
}
