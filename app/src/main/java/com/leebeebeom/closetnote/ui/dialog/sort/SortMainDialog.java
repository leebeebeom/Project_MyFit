package com.leebeebeom.closetnote.ui.dialog.sort;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.data.repository.CategoryRepository;
public class SortMainDialog extends BaseSortDialog {

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding.brandLayout.setVisibility(View.GONE);
    }

    @Override
    protected CategoryRepository getRepository() {
        return getMainGraphViewModel().getCategoryRepository();
    }

    @Override
    protected int getResId() {
        return R.id.sortMainDialog;
    }
}
