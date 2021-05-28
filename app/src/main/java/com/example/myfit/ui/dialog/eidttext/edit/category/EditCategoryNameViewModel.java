package com.example.myfit.ui.dialog.eidttext.edit.category;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditCategoryNameViewModel extends BaseEditTextViewModel.BaseEditViewModel {
    private final CategoryRepository mCategoryRepository;
    private String mName;
    private long mId;

    @Inject
    public EditCategoryNameViewModel(CategoryRepository categoryRepository) {
        this.mCategoryRepository = categoryRepository;
    }

    public void queryIsExistingName(String inputText, long id, int parentIndex) {
        this.mName = inputText;
        this.mId = id;
        mCategoryRepository.isExistingName(mName, parentIndex);
    }

    @Override
    public void update() {
        mCategoryRepository.update(mId, mName);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingMutable() {
        return null;
    }
}
