package com.example.myfit.ui.dialog.sort;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.databinding.LayoutDialogSortBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.util.constant.Sort;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseSortDialog extends BaseDialog {
    @Inject
    protected LayoutDialogSortBinding mBinding;

    private static final String CHECKED_SORT = "check sort";
    private int mCheckedNumber;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckedNumber = savedInstanceState == null ? getRepository().getSort().getValue() : savedInstanceState.getInt(CHECKED_SORT);
        MaterialRadioButton[] sortButtons = getSortButtons();
        addRadioButtonCheckListener(sortButtons);
        sortButtons[mCheckedNumber].setChecked(true);
    }

    protected abstract BaseRepository<?> getRepository();

    @NotNull
    private MaterialRadioButton[] getSortButtons() {
        return new MaterialRadioButton[]{mBinding.radioBtnCustom, mBinding.radioBtnCreate, mBinding.radioBtnCreateReverse,
                mBinding.radioBtnBrand, mBinding.radioBtnBrandReverse, mBinding.radioBtnName, mBinding.radioBtnNameReverse};
    }

    private void addRadioButtonCheckListener(@NotNull MaterialRadioButton[] buttons) {
        int[] buttonConstantArray = {Sort.SORT_CUSTOM.getValue(), Sort.SORT_CREATE.getValue(), Sort.SORT_CREATE_REVERSE.getValue(),
                Sort.SORT_BRAND.getValue(), Sort.SORT_BRAND_REVERSE.getValue(),
                Sort.SORT_NAME.getValue(), Sort.SORT_NAME_REVERSE.getValue()};

        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mCheckedNumber = buttonConstantArray[finalI];

                    Arrays.stream(buttons)
                            .filter(button -> button != buttonView)
                            .forEach(button -> button.setChecked(false));
                }
            });
        }
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder
                .setTitle(getString(R.string.all_sort_order))
                .setView(mBinding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            getRepository().changeSort(Sort.values()[mCheckedNumber]);
            dismiss();
        };
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHECKED_SORT, mCheckedNumber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
