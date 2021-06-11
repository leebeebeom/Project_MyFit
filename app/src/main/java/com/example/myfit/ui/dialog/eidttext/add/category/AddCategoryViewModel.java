package com.example.myfit.ui.dialog.eidttext.add.category;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCategoryViewModel extends BaseEditTextViewModel.BaseAddViewModel {
    private final CategoryRepository mCategoryRepository;
    private int mParentIndex;

    @Inject
    public AddCategoryViewModel(CategoryRepository categoryRepository) {
        this.mCategoryRepository = categoryRepository;
    }

    public void queryIsExistingName(int parentIndex) {
        this.mParentIndex = parentIndex;
        mCategoryRepository.isExistingName(mName.trim(), parentIndex);
    }

    @Override
    public void insert() {
        mCategoryRepository.insert(mName.trim(), mParentIndex);
    }

    @Override
    public MutableLiveData<Boolean> getExistingNameLive() {
        return mCategoryRepository.getExistingNameLive();
    }
}
