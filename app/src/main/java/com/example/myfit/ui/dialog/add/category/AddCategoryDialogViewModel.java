package com.example.myfit.ui.dialog.add.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.add.BaseAddViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCategoryDialogViewModel extends BaseAddViewModel {
    public static final String CATEGORY_NAME = "category name";
    private final SavedStateHandle savedStateHandle;
    private final CategoryRepository categoryRepository;
    private byte parentIndex;
    private String name;
    private final MediatorLiveData<Boolean> isExistingMutable = new MediatorLiveData<>();

    @Inject
    public AddCategoryDialogViewModel(@NotNull SavedStateHandle savedStateHandle, CategoryRepository categoryRepository) {
        this.savedStateHandle = savedStateHandle;
        this.categoryRepository = categoryRepository;

        LiveData<Boolean> isExistingLive = Transformations.switchMap(savedStateHandle.getLiveData(CATEGORY_NAME),
                name -> categoryRepository.isExistingName((String) name, this.parentIndex));
        isExistingMutable.addSource(isExistingLive, isExistingMutable::setValue);
    }

    @Override
    public void insert() {
        categoryRepository.insert(name, parentIndex);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingLive() {
        return isExistingMutable;
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
}
