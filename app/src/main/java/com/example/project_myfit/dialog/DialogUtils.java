package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.ALERT_TITLE;
import static com.example.project_myfit.MyFitConstant.ID;

public class DialogUtils {
    @NotNull
    public static ItemDialogEditTextBinding getCategoryBinding(LayoutInflater inflater, @NotNull Context context) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(inflater);
        binding.setHint(context.getString(R.string.category_name));
        binding.setPlaceHolder(context.getString(R.string.category_name_korean));
        binding.dialogEditText.requestFocus();
        return binding;
    }

    @NotNull
    public static ItemDialogEditTextBinding getCategoryBinding(LayoutInflater inflater, @NotNull Context context, String categoryName) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(inflater);
        binding.setHint(context.getString(R.string.category_name));
        binding.setPlaceHolder(context.getString(R.string.category_name_korean));
        binding.dialogEditText.requestFocus();
        binding.setSetText(categoryName);
        return binding;
    }

    @NotNull
    public static ItemDialogEditTextBinding getFolderBinding(LayoutInflater inflater, @NotNull Context context) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(inflater);
        binding.setHint(context.getString(R.string.folder_name));
        binding.setPlaceHolder(context.getString(R.string.folder_name_korean));
        binding.dialogEditText.requestFocus();
        return binding;
    }

    @NotNull
    public static ItemDialogEditTextBinding getFolderBinding(LayoutInflater inflater, @NotNull Context context, String folderName) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(inflater);
        binding.setHint(context.getString(R.string.folder_name));
        binding.setPlaceHolder(context.getString(R.string.folder_name_korean));
        binding.dialogEditText.requestFocus();
        binding.setSetText(folderName);
        return binding;
    }

    @NotNull
    public static AlertDialog getDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle(context.getString(R.string.confirm))
                .setMessage(message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, listener)
                .show();
        Window window = dialog.getWindow();
        setLayout(context, window);
        setTextSize(context, dialog);
        return dialog;
    }

    @NotNull
    public static AlertDialog getEditTextDialog(Context context, String title, @NotNull ItemDialogEditTextBinding binding, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.myAlertDialog)
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, listener)
                .create();
        Window window = dialog.getWindow();
        setKeyboard(window);
        setLayout(context, window);
        dialog.show();
        setTextSize(context, dialog);
        addTextChangeListener(binding, dialog);
        return dialog;
    }

    private static void setKeyboard(@NotNull Window window) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public static void setLayout(@NotNull Context context, @NotNull Window window) {
        int margin = (int) context.getResources().getDimension(R.dimen._20sdp);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.tree_view_dialog_background);
        InsetDrawable inset = new InsetDrawable(drawable, margin);
        window.setBackgroundDrawable(inset);
    }

    public static void setTextSize(@NotNull Context context, @NotNull AlertDialog dialog) {
        float titleSize = context.getResources().getDimension(R.dimen._5sdp);

        int titleId = context.getResources().getIdentifier(ALERT_TITLE, ID, context.getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);

        float size = context.getResources().getDimensionPixelSize(R.dimen._4sdp);

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        if (positive != null)
            positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        if (negative != null)
            negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        MaterialTextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            int padding = (int) context.getResources().getDimension(R.dimen._8sdp);
            message.setPadding(message.getPaddingLeft(), padding, message.getPaddingRight(), message.getPaddingBottom());
            message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
    }

    private static void addTextChangeListener(@NotNull ItemDialogEditTextBinding binding, @NotNull AlertDialog dialog) {
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setEnabled(false);
        binding.dialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positive.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.dialogEditText.setOnKeyListener((v, keyCode, event) -> {
            if (String.valueOf(binding.dialogEditText.getText()).length() == 30)
                binding.dialogEditTextLayout.setError("30자까지 입력 가능합니다.");
            else binding.dialogEditTextLayout.setErrorEnabled(false);
            return false;
        });
    }
}
