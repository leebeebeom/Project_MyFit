package com.example.myfit.ui.dialog.eidttext;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.DialogBindingBuilder;
import com.example.myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseEditTextDialog extends BaseDialog {
    public static final String INPUT_TEXT = "input text";
    @Inject
    DialogBindingBuilder mDialogBindingBuilder;
    private ItemDialogEditTextBinding mBinding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(savedInstanceState);
    }

    private ItemDialogEditTextBinding getBinding(@Nullable Bundle savedInstanceState) {
        return mDialogBindingBuilder
                .setHint(getHint())
                .setPlaceHolder(getPlaceHolder())
                .setText(getInputText(savedInstanceState))
                .create();
    }

    protected abstract String getHint();

    protected abstract String getPlaceHolder();

    protected abstract String getInputText(Bundle savedInstanceState);

    protected ItemDialogEditTextBinding getBinding() {
        return mBinding;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BaseEditTextViewModel model = getModel();
        model.getIsExistingMutable().observe(getViewLifecycleOwner(), isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    navigateSameNameDialog();
                    KeyBoardUtil.hideKeyBoard(mBinding.et);
                } else {
                    task();
                    dismiss();
                }
                model.getIsExistingMutable().setValue(null);
            }
        });
        return getAlertDialog(getDialogBuilder());
    }

    protected abstract BaseEditTextViewModel getModel();

    protected abstract void navigateSameNameDialog();

    protected abstract void task();

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INPUT_TEXT, getInputText());
    }

    @NotNull
    protected String getInputText() {
        return String.valueOf(mBinding.et.getText());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
