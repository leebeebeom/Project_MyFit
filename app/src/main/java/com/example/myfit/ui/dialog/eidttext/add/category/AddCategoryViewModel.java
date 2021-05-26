package com.example.myfit.ui.dialog.eidttext.add.category;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCategoryViewModel extends BaseEditTextViewModel.BaseAddViewModel {
    private final CategoryRepository categoryRepository;
    private int parentIndex;
    private String name;

    @Inject
    public AddCategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void queryIsExistingName(String inputText, int parentIndex) {
        this.name = inputText;
        this.parentIndex = parentIndex;
        categoryRepository.isExistingName(inputText, parentIndex);
    }

    @Override
    public void insert() {
        categoryRepository.insert(name, parentIndex);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingMutable() {
        return categoryRepository.getIsExistingNameLive();
    }
}
