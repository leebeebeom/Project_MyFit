package com.leebeebeom.closetnote.ui.dialog.eidttext;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leebeebeom.closetnote.databinding.ItemDialogEditTextBinding;
import com.leebeebeom.closetnote.ui.dialog.BaseDialog;
import com.leebeebeom.closetnote.util.KeyBoardUtil;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseEditTextDialog extends BaseDialog {
    public static final String INPUT_TEXT = "input text";
    protected ItemDialogEditTextBinding mBinding;
    private BaseEditTextViewModel mModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = getModel();

        mBinding = ItemDialogEditTextBinding.inflate(LayoutInflater.from(requireContext()));
        mBinding.et.requestFocus();

        if (mModel.getName() == null) mModel.setName(getName());
        mBinding.setLifecycleOwner(this);
        mBinding.setModel(mModel);
        mBinding.layout.setHint(getHint());
        mBinding.layout.setPlaceholderText(getPlaceHolder());
    }

    public abstract String getHint();

    public abstract String getPlaceHolder();

    protected abstract String getName();

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mModel.getExistingNameLive().observe(this, isExisting -> {
            if (isExisting) {
                navigateSameName();
                KeyBoardUtil.hideKeyBoard(mBinding.et);
            } else {
                task();
                dismiss();
            }
        });
        return getAlertDialog();
    }

    protected abstract BaseEditTextViewModel getModel();

    protected abstract void navigateSameName();

    protected abstract void task();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
