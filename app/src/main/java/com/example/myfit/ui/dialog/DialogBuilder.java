package com.example.myfit.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.myfit.R;
import com.example.myfit.util.OnTextChange;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DialogBuilder {
    public static final String ALERT_TITLE = "alertTitle";
    public static final String ID = "id";
    private final AlertDialog dialog;

    @Inject
    public DialogBuilder(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public DialogBuilder makeConfirmDialog(String message) {
        dialog.setTitle(dialog.getContext().getString(R.string.dialog_confirm));
        dialog.setMessage(message);
        return this;
    }

    public DialogBuilder makeEditTextDialog(String title, View view) {
        dialog.setTitle(title);
        dialog.setView(view);
        return this;
    }

    public DialogBuilder setTitle(String title){
        dialog.setTitle(title);
        return this;
    }

    public DialogBuilder setView(View view){
        dialog.setView(view);
        return this;
    }

    private void showKeyboard() {
        if (dialog.isShowing()) dialog.dismiss();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public DialogBuilder setPositiveEnabledByInputText(CharSequence inputText) {
        getPositiveButton().setEnabled(!TextUtils.isEmpty(inputText));
        return this;
    }

    public DialogBuilder setPositiveEnabledByInputText(CharSequence inputText, String oldName) {
        getPositiveButton().setEnabled(!TextUtils.isEmpty(inputText) && !inputText.equals(oldName));
        return this;
    }

    public DialogBuilder setPositiveEnabledByChangedText(@NotNull TextInputEditText editText) {
        editText.addTextChangedListener(new OnTextChange() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPositiveEnabledByInputText(s);
            }
        });
        return this;
    }

    public DialogBuilder setPositiveEnabledByChangedText(@NotNull TextInputEditText editText, String oldName) {
        editText.addTextChangedListener(new OnTextChange() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPositiveEnabledByInputText(s, oldName);
            }
        });
        return this;
    }

    public DialogBuilder setPositiveClickListener(View.OnClickListener listener) {
        getPositiveButton().setOnClickListener(listener);
        return this;
    }

    public DialogBuilder setPositiveCallOnClickWhenImeClicked(@NotNull TextInputEditText editText) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && getPositiveButton().isEnabled())
                getPositiveButton().callOnClick();
            return false;
        });
        return this;
    }

    private Button getPositiveButton() {
        showDialog();
        return dialog.getButton(DialogInterface.BUTTON_POSITIVE);
    }

    public AlertDialog create() {
        this.setBackgroundDrawable();
        this.setTitleTextSize();
        this.setMessageTextSize();
        this.setMessageTopPadding();
        this.setButtonTextSize();
        this.showKeyboard();
        return dialog;
    }

    private void setBackgroundDrawable() {
        int margin = (int) dialog.getContext().getResources().getDimension(R.dimen._20sdp);
        Drawable dialogBackground = ContextCompat.getDrawable(dialog.getContext(), R.drawable.tree_view_dialog_background);
        InsetDrawable inset = new InsetDrawable(dialogBackground, margin);
        dialog.getWindow().setBackgroundDrawable(inset);
    }

    private void setTitleTextSize() {
        showDialog();
        TextView tvTitle = dialog.findViewById(dialog.getContext().getResources().getIdentifier(ALERT_TITLE, ID, dialog.getContext().getPackageName()));
        if (tvTitle != null)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dialog.getContext().getResources().getDimension(R.dimen._5sdp));
    }

    private void setMessageTextSize() {
        showDialog();
        TextView tvMessage = dialog.findViewById(android.R.id.message);
        if (tvMessage != null)
            tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dialog.getContext().getResources().getDimension(R.dimen._4sdp));
    }

    private void setMessageTopPadding() {
        showDialog();
        TextView tvMessage = dialog.findViewById(android.R.id.message);
        if (tvMessage != null)
            tvMessage.setPadding(tvMessage.getPaddingLeft(), dialog.getContext().getResources().getDimensionPixelSize(R.dimen._8sdp), tvMessage.getPaddingRight(), tvMessage.getPaddingBottom());
    }

    private void setButtonTextSize() {
        showDialog();
        Button btnPositive = getPositiveButton();
        btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dialog.getContext().getResources().getDimension(R.dimen._4sdp));
        Button btnNegative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dialog.getContext().getResources().getDimension(R.dimen._4sdp));
    }

    private void showDialog() {
        if (!dialog.isShowing()) dialog.show();
    }
}
