package com.example.myfit.ui.dialog.sort;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myfit.R;
import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.databinding.LayoutDialogSortBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortMainDialog extends BaseSortDialog {
    @Inject
    CategoryRepository mCategoryRepository;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutDialogSortBinding binding = getBinding();
        binding.radioBtnBrand.setVisibility(View.GONE);
        binding.radioBtnBrandReverse.setVisibility(View.GONE);
    }

    @Override
    protected int getCheckedNumber() {
        return mCategoryRepository.getSort().getValue();
    }

    @Override
    protected BaseRepository getRepository() {
        return mCategoryRepository;
    }

    @Override
    protected int getResId() {
        return R.id.sortMainDialog;
    }
}
