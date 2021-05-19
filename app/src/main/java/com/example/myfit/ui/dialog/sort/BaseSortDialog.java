package com.example.myfit.ui.dialog.sort;

import android.app.Dialog;
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
import com.example.myfit.util.constant.SortValue;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class BaseSortDialog extends BaseDialog {
    private LayoutDialogSortBinding binding;
    private int checkedNumber;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding();
        checkedNumber = savedInstanceState == null ? getCheckedNumber() : savedInstanceState.getInt(Sort.SORT.getText());
        MaterialRadioButton[] sortButtons = getSortButtons();
        addRadioButtonCheckListener(sortButtons);
        sortButtons[checkedNumber].setChecked(true);
    }

    protected abstract LayoutDialogSortBinding getBinding();

    protected abstract int getCheckedNumber();

    @NotNull
    private MaterialRadioButton[] getSortButtons() {
        return new MaterialRadioButton[]{binding.radioBtnCustom, binding.radioBtnCreate, binding.radioBtnCreateReverse,
                binding.radioBtnBrand, binding.radioBtnBrandReverse, binding.radioBtnName, binding.radioBtnNameReverse};
    }

    private void addRadioButtonCheckListener(@NotNull MaterialRadioButton[] buttons) {
        int[] buttonConstantArray = {SortValue.SORT_CUSTOM.getValue(), SortValue.SORT_CREATE.getValue(), SortValue.SORT_CREATE_REVERSE.getValue(),
                SortValue.SORT_BRAND.getValue(), SortValue.SORT_BRAND_REVERSE.getValue(),
                SortValue.SORT_NAME.getValue(), SortValue.SORT_NAME_REVERSE.getValue()};

        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkedNumber = buttonConstantArray[finalI];

                    Arrays.stream(buttons)
                            .filter(button -> button != buttonView)
                            .forEach(button -> button.setChecked(false));
                }
            });
        }
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder
                .setTitle(getString(R.string.all_sort_order))
                .setView(binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            getRepository().changeSort(checkedNumber);
            dismiss();
        };
    }

    protected abstract BaseRepository getRepository();

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Sort.SORT.getText(), checkedNumber);
    }
}
