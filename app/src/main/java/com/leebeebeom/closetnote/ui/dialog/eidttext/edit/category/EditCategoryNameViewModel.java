package com.leebeebeom.closetnote.ui.dialog.eidttext.edit.category;

import androidx.lifecycle.MutableLiveData;

import com.leebeebeom.closetnote.data.repository.CategoryRepository;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;

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
        mCategoryRepository.update(mId, mName, 0);
    }

    @Override
    public MutableLiveData<Boolean> getExistingNameLive() {
        return mCategoryRepository.getExistingNameLive();
    }
}
