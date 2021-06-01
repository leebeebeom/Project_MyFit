package com.example.myfit.ui.dialog.sort;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myfit.R;
import com.example.myfit.data.repository.CategoryRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortMainDialog extends BaseSortDialog {
    @Inject
    CategoryRepository mCategoryRepository;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding.radioBtnBrand.setVisibility(View.GONE);
        mBinding.radioBtnBrandReverse.setVisibility(View.GONE);
    }

    @Override
    protected CategoryRepository getRepository() {
        return mCategoryRepository;
    }

    @Override
    protected int getResId() {
        return R.id.sortMainDialog;
    }
}
