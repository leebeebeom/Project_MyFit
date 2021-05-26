package com.example.myfit.ui.dialog.sort;

import android.view.View;

import com.example.myfit.R;
import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.databinding.LayoutDialogSortBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortMainDialog extends BaseSortDialog {
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    LayoutDialogSortBinding binding;

    @Override
    protected LayoutDialogSortBinding getBinding() {
        binding.radioBtnBrand.setVisibility(View.GONE);
        binding.radioBtnBrandReverse.setVisibility(View.GONE);
        return binding;
    }

    @Override
    protected int getCheckedNumber() {
        return categoryRepository.getSort().getValue();
    }

    @Override
    protected BaseRepository getRepository() {
        return categoryRepository;
    }

    @Override
    protected int getResId() {
        return R.id.sortMainDialog;
    }
}
