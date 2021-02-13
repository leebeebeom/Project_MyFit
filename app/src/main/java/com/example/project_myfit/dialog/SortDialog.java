package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.SortViewBinding;
import com.example.project_myfit.ui.main.MainFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static com.example.project_myfit.MyFitConstant.ALERT_TITLE;
import static com.example.project_myfit.MyFitConstant.ID;
import static com.example.project_myfit.MyFitConstant.SORT;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.MyFitConstant.SORT_NAME_REVERSE;

public class SortDialog extends DialogFragment {
    private AtomicInteger mCheckedItem;
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
        mCheckedItem = new AtomicInteger();
        if (getArguments() != null) mCheckedItem.set(getArguments().getInt(SORT));
        if (savedInstanceState != null) mCheckedItem.set(savedInstanceState.getInt(SORT));

        SortViewBinding binding = SortViewBinding.inflate(getLayoutInflater());

        binding.sortCustom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCheckedItem.set(SORT_CUSTOM);
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
                mCheckedItem.set(SORT_CREATE);
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
                mCheckedItem.set(SORT_CREATE_REVERSE);
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
                mCheckedItem.set(SORT_BRAND);
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
                mCheckedItem.set(SORT_BRAND_REVERSE);
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
                mCheckedItem.set(SORT_NAME);
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
                mCheckedItem.set(SORT_NAME_REVERSE);
                binding.sortCustom.setChecked(false);
                binding.sortCreate.setChecked(false);
                binding.sortCreateReverse.setChecked(false);
                binding.sortBrand.setChecked(false);
                binding.sortBrandReverse.setChecked(false);
                binding.sortName.setChecked(false);
            }
        });

        if (mCheckedItem.get() == SORT_CUSTOM) binding.sortCustom.setChecked(true);
        else if (mCheckedItem.get() == SORT_CREATE) binding.sortCreate.setChecked(true);
        else if (mCheckedItem.get() == SORT_CREATE_REVERSE)
            binding.sortCreateReverse.setChecked(true);
        else if (mCheckedItem.get() == SORT_BRAND) binding.sortBrand.setChecked(true);
        else if (mCheckedItem.get() == SORT_BRAND_REVERSE)
            binding.sortBrandReverse.setChecked(true);
        else if (mCheckedItem.get() == SORT_NAME) binding.sortName.setChecked(true);
        else if (mCheckedItem.get() == SORT_NAME_REVERSE) binding.sortNameReverse.setChecked(true);

        if (getTargetFragment() instanceof MainFragment) {
            binding.sortBrand.setVisibility(View.GONE);
            binding.sortBrandReverse.setVisibility(View.GONE);
        }


        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle(R.string.sort)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog1, which) -> mListener.sortConfirmClick(mCheckedItem.get()))
                .show();

        float size = getResources().getDimensionPixelSize(R.dimen._4sdp);
        float titleSize = getResources().getDimension(R.dimen._5sdp);

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        int titleId = getResources().getIdentifier(ALERT_TITLE, ID, requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);

        Window window = dialog.getWindow();
        int margin = (int) requireContext().getResources().getDimension(R.dimen._20sdp);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tree_view_dialog_background);
        InsetDrawable inset = new InsetDrawable(drawable, margin);
        window.setBackgroundDrawable(inset);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT, mCheckedItem.get());
    }

    public interface SortConfirmClick {
        void sortConfirmClick(int which);
    }
}