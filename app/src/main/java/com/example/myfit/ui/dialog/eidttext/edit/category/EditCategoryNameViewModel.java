package com.example.myfit.ui.dialog.eidttext.edit.category;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditCategoryNameViewModel extends BaseEditTextViewModel.BaseEditViewModel {
    private final CategoryRepository categoryRepository;
    private String name;
    private long id;

    @Inject
    public EditCategoryNameViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void queryIsExistingName(String inputText, long id, int parentIndex) {
        this.name = inputText;
        this.id = id;
        categoryRepository.isExistingName(name, parentIndex);
    }

    @Override
    public void update() {
        categoryRepository.update(id, name);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingMutable() {
        return null;
    }
}
