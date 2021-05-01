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
import com.example.project_myfit.databinding.LayoutDialogSortBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.MAIN_FRAGMENT;
import static com.example.project_myfit.util.MyFitConstant.SORT;
import static com.example.project_myfit.util.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.util.MyFitConstant.SORT_BRAND_REVERSE;
import static com.example.project_myfit.util.MyFitConstant.SORT_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.util.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.util.MyFitConstant.SORT_NAME_REVERSE;

public class SortDialog extends DialogFragment {
    private int mCheckedItem;
    private String mFragmentType;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckedItem = savedInstanceState == null ? SortDialogArgs.fromBundle(getArguments()).getSort() : savedInstanceState.getInt(SORT);
        mFragmentType = SortDialogArgs.fromBundle(getArguments()).getFragmentType();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main).backStackLiveSetValue(R.id.sortDialog);

        LayoutDialogSortBinding binding = LayoutDialogSortBinding.inflate(getLayoutInflater());
        MaterialRadioButton[] sortButtons = {binding.radioBtnCustom, binding.radioBtnCreate, binding.radioBtnCreateReverse,
                binding.radioBtnBrand, binding.radioBtnBrandReverse, binding.radioBtnName, binding.radioBtnNameReverse};
        addCheckListener(sortButtons);
        sortButtons[mCheckedItem].setChecked(true);

        //메인프래그먼트에서 실행 시 브랜드 삭제
        if (mFragmentType.equals(MAIN_FRAGMENT)) {
            binding.radioBtnBrand.setVisibility(View.GONE);
            binding.radioBtnBrandReverse.setVisibility(View.GONE);
        }

        AlertDialog alertDialog = getDialog(dialogUtils, binding);

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

    private AlertDialog getDialog(DialogUtils dialogUtils, @NotNull LayoutDialogSortBinding binding) {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialogStyle)
                .setTitle(R.string.all_sort_order)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) ->
                        dialogUtils.getBackStackEntry().getSavedStateHandle().set(SORT_CONFIRM, mCheckedItem))
                .show();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT, mCheckedItem);
    }
}