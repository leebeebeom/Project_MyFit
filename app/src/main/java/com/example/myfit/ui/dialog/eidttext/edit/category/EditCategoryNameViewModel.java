package com.example.myfit.ui.dialog.eidttext.edit.category;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditCategoryNameViewModel extends BaseEditTextViewModel.BaseEditViewModel {
    private final CategoryRepository mCategoryRepository;
    private long mId;

    @Inject
    public EditCategoryNameViewModel(CategoryRepository categoryRepository) {
        this.mCategoryRepository = categoryRepository;
    }

    public void queryIsExistingName(long id, int parentIndex) {
        this.mId = id;
        mCategoryRepository.isExistingName(mName, parentIndex);
    }

    @Override
    public void update() {
        mCategoryRepository.update(mId, mName);
    }

    @Override
    public MutableLiveData<Boolean> getExistingNameLive() {
        return mCategoryRepository.getExistingNameLive();
    }
}
