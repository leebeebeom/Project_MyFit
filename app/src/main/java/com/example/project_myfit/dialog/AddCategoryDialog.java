package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.ALERT_TITLE;
import static com.example.project_myfit.MyFitConstant.ID;

public class AddCategoryDialog extends DialogFragment {
    public interface AddCategoryConfirmClick {
        void addCategoryConfirmClick(String categoryName);
    }

    private AddCategoryConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (AddCategoryConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(getLayoutInflater());

        binding.setHint(getString(R.string.category_name));
        binding.editTextDialog.requestFocus();
        binding.editTextLayoutDialog.setPlaceholderText(getString(R.string.category_name_korean));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle(R.string.add_category)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog1, which) -> mListener.addCategoryConfirmClick(String.valueOf(binding.editTextDialog.getText())))
                .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        float size = getResources().getDimensionPixelSize(R.dimen._4sdp);
        float titleSize = getResources().getDimension(R.dimen._5sdp);

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        positive.setEnabled(false);

        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        binding.editTextDialog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positive.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        int titleId = getResources().getIdentifier(ALERT_TITLE, ID, requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);

        return dialog;
    }
}
