package com.example.myfit.ui.dialog.add.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.repository.CategoryRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCategoryDialogViewModel extends ViewModel {
    public static final String CATEGORY_NAME = "category name";
    private final CategoryRepository categoryRepository;
    private final SavedStateHandle savedStateHandle;
    private byte parentIndex;
    private String name;
    private LiveData<Boolean> isExistingLive = new MutableLiveData<>();

    @Inject
    public AddCategoryDialogViewModel(SavedStateHandle savedStateHandle, CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.savedStateHandle = savedStateHandle;
    }

    public void queryIsExistingName(String name) {
        this.name = name;
        isExistingLive = Transformations.switchMap(savedStateHandle.getLiveData(CATEGORY_NAME), name2 -> categoryRepository.isExistingName((String) name2, parentIndex));
        savedStateHandle.set(CATEGORY_NAME, name);
    }

    public void insert() {
        categoryRepository.insert(name, parentIndex);
    }

    public LiveData<Boolean> getIsExistingLive() {
        return isExistingLive;
    }

    public void setParentIndex(byte parentIndex) {
        this.parentIndex = parentIndex;
    }
}
