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
    private final AlertDialog mDialog;

    @Inject
    public DialogBuilder(AlertDialog dialog) {
        this.mDialog = dialog;
    }

    public DialogBuilder makeConfirmDialog(String message) {
        mDialog.setTitle(mDialog.getContext().getString(R.string.dialog_confirm));
        mDialog.setMessage(message);
        return this;
    }

    public DialogBuilder makeEditTextDialog(String title, View view) {
        mDialog.setTitle(title);
        mDialog.setView(view);
        return this;
    }

    public DialogBuilder setTitle(String title){
        mDialog.setTitle(title);
        return this;
    }

    public DialogBuilder setView(View view){
        mDialog.setView(view);
        return this;
    }

    public DialogBuilder setPositiveEnabledByInputText(CharSequence inputText) {
        getPositiveButton().setEnabled(isNotInputTextEmpty(inputText));
        return this;
    }

    public DialogBuilder setPositiveEnabledByInputText(CharSequence inputText, String oldName) {
        getPositiveButton().setEnabled(isNotInputTextEmpty(inputText) && !inputText.toString().equals(oldName));
        return this;
    }

    private boolean isNotInputTextEmpty(CharSequence inputText) {
        return !TextUtils.isEmpty(inputText.toString().trim());
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
        return mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
    }

    public AlertDialog create() {
        this.setBackgroundDrawable();
        this.setTitleTextSize();
        this.setMessageTextSize();
        this.setMessageTopPadding();
        this.setButtonTextSize();
        this.showKeyboard();
        return mDialog;
    }

    private void setBackgroundDrawable() {
        int margin = (int) mDialog.getContext().getResources().getDimension(R.dimen._20sdp);
        Drawable dialogBackground = ContextCompat.getDrawable(mDialog.getContext(), R.drawable.tree_view_dialog_background);
        InsetDrawable inset = new InsetDrawable(dialogBackground, margin);
        mDialog.getWindow().setBackgroundDrawable(inset);
    }

    private void setTitleTextSize() {
        showDialog();
        TextView tvTitle = mDialog.findViewById(mDialog.getContext().getResources().getIdentifier(ALERT_TITLE, ID, mDialog.getContext().getPackageName()));
        if (tvTitle != null)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mDialog.getContext().getResources().getDimension(R.dimen._5sdp));
    }

    private void setMessageTextSize() {
        showDialog();
        TextView tvMessage = mDialog.findViewById(android.R.id.message);
        if (tvMessage != null)
            tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mDialog.getContext().getResources().getDimension(R.dimen._4sdp));
    }

    private void setMessageTopPadding() {
        showDialog();
        TextView tvMessage = mDialog.findViewById(android.R.id.message);
        if (tvMessage != null)
            tvMessage.setPadding(tvMessage.getPaddingLeft(), mDialog.getContext().getResources().getDimensionPixelSize(R.dimen._8sdp), tvMessage.getPaddingRight(), tvMessage.getPaddingBottom());
    }

    private void setButtonTextSize() {
        showDialog();
        Button btnPositive = getPositiveButton();
        btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mDialog.getContext().getResources().getDimension(R.dimen._4sdp));
        Button btnNegative = mDialog.getButton(Dialog.BUTTON_NEGATIVE);
        btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mDialog.getContext().getResources().getDimension(R.dimen._4sdp));
    }

    private void showKeyboard() {
        if (mDialog.isShowing()) mDialog.dismiss();
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void showDialog() {
        if (!mDialog.isShowing()) mDialog.show();
    }
}
