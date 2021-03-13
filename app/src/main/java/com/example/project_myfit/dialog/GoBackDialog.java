package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

public class GoBackDialog extends DialogFragment {
    private GoBackConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (GoBackConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return DialogUtils.getDialog(requireContext(), getString(R.string.go_back_check),
                (dialog, which) -> mListener.goBackConfirmClick());
    }

    public interface GoBackConfirmClick {
        void goBackConfirmClick();
    }
}
