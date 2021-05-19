package com.example.myfit.ui.dialog.sort;

import android.view.View;

import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.databinding.LayoutDialogSortBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortCategoryDialog extends BaseSortDialog {
    @Inject
    CategoryRepository categoryRepository;

    @Override
    protected LayoutDialogSortBinding getBinding() {
        LayoutDialogSortBinding binding = LayoutDialogSortBinding.inflate(getLayoutInflater());
        binding.radioBtnBrand.setVisibility(View.GONE);
        binding.radioBtnBrandReverse.setVisibility(View.GONE);
        return binding;
    }

    @Override
    protected int getCheckedNumber() {
        return categoryRepository.getSort();
    }

    @Override
    protected BaseRepository getRepository() {
        return categoryRepository;
    }
}