package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.SortViewBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.MAIN_FRAGMENT;
import static com.example.project_myfit.MyFitConstant.SORT;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.MyFitConstant.SORT_NAME_REVERSE;

public class SortDialog extends DialogFragment {
    private int mCheckedItem;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mCheckedItem = savedInstanceState == null ? SortDialogArgs.fromBundle(getArguments()).getSort() : savedInstanceState.getInt(SORT);
        String fragmentType = SortDialogArgs.fromBundle(getArguments()).getFragmentType();

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this).setBackStack(R.id.sortDialog);

        SortViewBinding binding = SortViewBinding.inflate(getLayoutInflater());
        MaterialRadioButton[] sortButtons = {binding.sortCustom, binding.sortCreate, binding.sortCreateReverse,
                binding.sortBrand, binding.sortBrandReverse, binding.sortName, binding.sortNameReverse};
        addCheckListener(sortButtons);
        sortButtons[mCheckedItem].setChecked(true);

        //메인프래그먼트에서 실행 시 브랜드 삭제
        if (fragmentType.equals(MAIN_FRAGMENT)) {
            binding.sortBrand.setVisibility(View.GONE);
            binding.sortBrandReverse.setVisibility(View.GONE);
        }

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle(R.string.sort)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog, which) -> dialogUtils.sortConfirmClick(mCheckedItem))
                .show();

        Window window = alertDialog.getWindow();
        dialogUtils.setLayout(window);
        dialogUtils.setTextSize(alertDialog);
        return alertDialog;
    }

    private void addCheckListener(@NotNull MaterialRadioButton[] buttons) {
        buttons[0].setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_CUSTOM;
                for (MaterialRadioButton button : buttons)
                    if (button != buttonView) button.setChecked(false);
            }
        });
        buttons[1].setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_CREATE;
                for (MaterialRadioButton button : buttons)
                    if (button != buttonView) button.setChecked(false);
            }
        });
        buttons[2].setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_CREATE_REVERSE;
                for (MaterialRadioButton button : buttons)
                    if (button != buttonView) button.setChecked(false);
            }
        });
        buttons[3].setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_BRAND;
                for (MaterialRadioButton button : buttons)
                    if (button != buttonView) button.setChecked(false);
            }
        });
        buttons[4].setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_BRAND_REVERSE;
                for (MaterialRadioButton button : buttons)
                    if (button != buttonView) button.setChecked(false);
            }
        });
        buttons[5].setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_NAME;
                for (MaterialRadioButton button : buttons)
                    if (button != buttonView) button.setChecked(false);
            }
        });
        buttons[6].setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_NAME_REVERSE;
                for (MaterialRadioButton button : buttons)
                    if (button != buttonView) button.setChecked(false);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT, mCheckedItem);
    }
}