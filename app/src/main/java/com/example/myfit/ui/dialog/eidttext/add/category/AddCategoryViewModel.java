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
    private String mName;

    @Inject
    public AddCategoryViewModel(CategoryRepository categoryRepository) {
        this.mCategoryRepository = categoryRepository;
    }

    public void queryIsExistingName(String inputText, int parentIndex) {
        this.mName = inputText;
        this.mParentIndex = parentIndex;
        mCategoryRepository.isExistingName(inputText, parentIndex);
    }

    @Override
    public void insert() {
        mCategoryRepository.insert(mName, mParentIndex);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingMutable() {
        return mCategoryRepository.getExistingNameLive();
    }
}
