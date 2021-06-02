package com.example.myfit.ui.dialog.eidttext;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@AndroidEntryPoint
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEditTextDialog extends BaseDialog {
    public static final String INPUT_TEXT = "input text";
    @Inject
    protected ItemDialogEditTextBinding mBinding;
    private BaseEditTextViewModel mModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding.et.setHint(getHint());
        mBinding.layout.setPlaceholderText(getPlaceHolder());
        mModel = getModel();
        mModel.setName(getInitialText(savedInstanceState));
        mBinding.setLifecycleOwner(this);
        mBinding.setModel(mModel);
    }

    protected abstract String getHint();

    protected abstract String getPlaceHolder();

    protected abstract String getInitialText(Bundle savedInstanceState);

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mModel.getExistingNameLive().observe(getViewLifecycleOwner(), isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    navigateSameNameDialog();
                    KeyBoardUtil.hideKeyBoard(mBinding.et);
                } else {
                    task();
                    dismiss();
                }
                mModel.getExistingNameLive().setValue(null);
            }
        });
        return getAlertDialog();
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
