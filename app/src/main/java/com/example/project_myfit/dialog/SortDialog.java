package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_LATEST;
import static com.example.project_myfit.MyFitConstant.SORT_LATEST_REVERSE;

public class SortDialog extends DialogFragment {

    private AtomicInteger mCheckedItem;
    private SortConfirmClick mListener;

    public SortDialog() {
    }

    public static SortDialog getInstance(int sort) {
        SortDialog sortDialog = new SortDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("sort", sort);
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
        if (getArguments() != null) mCheckedItem.set(getArguments().getInt("sort"));
        if (savedInstanceState != null) mCheckedItem.set(savedInstanceState.getInt("sort"));

        String[] items = new String[]{getString(R.string.sort_custom), getString(R.string.sort_latest), getString(R.string.sort_latest_reverse)};
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle(R.string.sort)
                .setSingleChoiceItems(items, mCheckedItem.get(), (dialog1, which) -> {
                    if (which == SORT_CUSTOM) mCheckedItem.set(SORT_CUSTOM);
                    else if (which == SORT_LATEST) mCheckedItem.set(SORT_LATEST);
                    else mCheckedItem.set(SORT_LATEST_REVERSE);
                })
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog1, which) -> mListener.sortConfirmClick(mCheckedItem.get()))
                .show();

        float size = getResources().getDimensionPixelSize(R.dimen._4sdp);
        float titleSize = getResources().getDimension(R.dimen._5sdp);

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        int titleId = getResources().getIdentifier("alertTitle", "id", requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sort", mCheckedItem.get());
    }

    public interface SortConfirmClick {
        void sortConfirmClick(int which);
    }
}