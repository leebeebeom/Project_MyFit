package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.SortViewBinding;
import com.example.project_myfit.ui.main.MainFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

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
    private SortConfirmClick mListener;

    public SortDialog() {
    }

    @NotNull
    public static SortDialog getInstance(int sort) {
        SortDialog sortDialog = new SortDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(SORT, sort);
        sortDialog.setArguments(bundle);
        return sortDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (SortConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getArguments() != null) mCheckedItem = getArguments().getInt(SORT);
        if (savedInstanceState != null) mCheckedItem = savedInstanceState.getInt(SORT);

        SortViewBinding binding = SortViewBinding.inflate(getLayoutInflater());
        addCheckListener(binding);
        checkLastedCheckedItem(binding);

        //for mainFragment
        if (getTargetFragment() instanceof MainFragment) {
            binding.sortBrand.setVisibility(View.GONE);
            binding.sortBrandReverse.setVisibility(View.GONE);
        }

        //create dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle(R.string.sort)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog1, which) -> mListener.sortConfirmClick(mCheckedItem))
                .show();
        Window window = dialog.getWindow();
        DialogUtils.setLayout(requireContext(), window);
        DialogUtils.setTextSize(requireContext(), dialog);

        return dialog;
    }

    private void addCheckListener(@NotNull SortViewBinding binding) {
        binding.sortCustom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_CUSTOM;
                binding.sortCreate.setChecked(false);
                binding.sortCreateReverse.setChecked(false);
                binding.sortBrand.setChecked(false);
                binding.sortBrandReverse.setChecked(false);
                binding.sortName.setChecked(false);
                binding.sortNameReverse.setChecked(false);
            }
        });
        binding.sortCreate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_CREATE;
                binding.sortCustom.setChecked(false);
                binding.sortCreateReverse.setChecked(false);
                binding.sortBrand.setChecked(false);
                binding.sortBrandReverse.setChecked(false);
                binding.sortName.setChecked(false);
                binding.sortNameReverse.setChecked(false);
            }
        });
        binding.sortCreateReverse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_CREATE_REVERSE;
                binding.sortCustom.setChecked(false);
                binding.sortCreate.setChecked(false);
                binding.sortBrand.setChecked(false);
                binding.sortBrandReverse.setChecked(false);
                binding.sortName.setChecked(false);
                binding.sortNameReverse.setChecked(false);
            }
        });
        binding.sortBrand.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_BRAND;
                binding.sortCustom.setChecked(false);
                binding.sortCreate.setChecked(false);
                binding.sortCreateReverse.setChecked(false);
                binding.sortBrandReverse.setChecked(false);
                binding.sortName.setChecked(false);
                binding.sortNameReverse.setChecked(false);
            }
        });
        binding.sortBrandReverse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_BRAND_REVERSE;
                binding.sortCustom.setChecked(false);
                binding.sortCreate.setChecked(false);
                binding.sortCreateReverse.setChecked(false);
                binding.sortBrand.setChecked(false);
                binding.sortName.setChecked(false);
                binding.sortNameReverse.setChecked(false);
            }
        });
        binding.sortName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_NAME;
                binding.sortCustom.setChecked(false);
                binding.sortCreate.setChecked(false);
                binding.sortCreateReverse.setChecked(false);
                binding.sortBrand.setChecked(false);
                binding.sortBrandReverse.setChecked(false);
                binding.sortNameReverse.setChecked(false);
            }
        });
        binding.sortNameReverse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem = SORT_NAME_REVERSE;
                binding.sortCustom.setChecked(false);
                binding.sortCreate.setChecked(false);
                binding.sortCreateReverse.setChecked(false);
                binding.sortBrand.setChecked(false);
                binding.sortBrandReverse.setChecked(false);
                binding.sortName.setChecked(false);
            }
        });
    }

    private void checkLastedCheckedItem(SortViewBinding binding) {
        if (mCheckedItem == SORT_CUSTOM) binding.sortCustom.setChecked(true);
        else if (mCheckedItem == SORT_CREATE) binding.sortCreate.setChecked(true);
        else if (mCheckedItem == SORT_CREATE_REVERSE)
            binding.sortCreateReverse.setChecked(true);
        else if (mCheckedItem == SORT_BRAND) binding.sortBrand.setChecked(true);
        else if (mCheckedItem == SORT_BRAND_REVERSE)
            binding.sortBrandReverse.setChecked(true);
        else if (mCheckedItem == SORT_NAME) binding.sortName.setChecked(true);
        else if (mCheckedItem == SORT_NAME_REVERSE) binding.sortNameReverse.setChecked(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT, mCheckedItem);
    }

    public interface SortConfirmClick {
        void sortConfirmClick(int which);
    }
}