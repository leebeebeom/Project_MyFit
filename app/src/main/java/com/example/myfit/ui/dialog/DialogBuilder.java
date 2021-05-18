package com.example.myfit.ui.dialog;

import android.app.Dialog;
import android.content.Context;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DialogBuilder {
    public static final String ALERT_TITLE = "alertTitle";
    public static final String ID = "id";
    private final AlertDialog mDialog;
    private final Context mContext;

    @Inject
    public DialogBuilder(Context context) {
        this.mContext = context;
        this.mDialog = new MaterialAlertDialogBuilder(context, R.style.myAlertDialogStyle)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create();
    }

    public DialogBuilder makeConfirmDialog(String message) {
        mDialog.setTitle(mContext.getString(R.string.dialog_confirm));
        mDialog.setMessage(message);
        return this;
    }

    public DialogBuilder makeEditTextDialog(String title, View view) {
        mDialog.setTitle(title);
        mDialog.setView(view);
        return this;
    }

    private void showKeyboard() {
        if (mDialog.isShowing()) mDialog.dismiss();
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public DialogBuilder setPositiveEnabledByIsInputText(CharSequence inputText) {
        getPositiveButton().setEnabled(!TextUtils.isEmpty(inputText));
        return this;
    }

    public DialogBuilder setPositiveEnabledByIsInputText(CharSequence inputText, String oldName) {
        getPositiveButton().setEnabled(!TextUtils.isEmpty(inputText) && !inputText.equals(oldName));
        return this;
    }

    public DialogBuilder setPositiveEnabledByIsChangedText(@NotNull TextInputEditText editText) {
        editText.addTextChangedListener(new OnTextChange() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPositiveEnabledByIsInputText(s);
            }
        });
        return this;
    }

    public DialogBuilder setPositiveEnabledByIsChangedText(@NotNull TextInputEditText editText, String oldName) {
        editText.addTextChangedListener(new OnTextChange() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPositiveEnabledByIsInputText(s, oldName);
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
        int margin = (int) mContext.getResources().getDimension(R.dimen._20sdp);
        Drawable dialogBackground = ContextCompat.getDrawable(mContext, R.drawable.tree_view_dialog_background);
        InsetDrawable inset = new InsetDrawable(dialogBackground, margin);
        mDialog.getWindow().setBackgroundDrawable(inset);
    }

    private void setTitleTextSize() {
        showDialog();
        TextView tvTitle = mDialog.findViewById(mContext.getResources().getIdentifier(ALERT_TITLE, ID, mContext.getPackageName()));
        if (tvTitle != null)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mContext.getResources().getDimension(R.dimen._5sdp));
    }

    private void setMessageTextSize() {
        showDialog();
        TextView tvMessage = mDialog.findViewById(android.R.id.message);
        if (tvMessage != null)
            tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mContext.getResources().getDimension(R.dimen._4sdp));
    }

    private void setMessageTopPadding() {
        showDialog();
        TextView tvMessage = mDialog.findViewById(android.R.id.message);
        if (tvMessage != null)
            tvMessage.setPadding(tvMessage.getPaddingLeft(), mContext.getResources().getDimensionPixelSize(R.dimen._8sdp), tvMessage.getPaddingRight(), tvMessage.getPaddingBottom());
    }

    private void setButtonTextSize() {
        showDialog();
        Button btnPositive = getPositiveButton();
        btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mContext.getResources().getDimension(R.dimen._4sdp));
        Button btnNegative = mDialog.getButton(Dialog.BUTTON_NEGATIVE);
        btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mContext.getResources().getDimension(R.dimen._4sdp));
    }

    private void showDialog() {
        if (!mDialog.isShowing()) mDialog.show();
    }
}
