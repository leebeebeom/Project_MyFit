package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.FOLDER;
import static com.example.project_myfit.MyFitConstant.FOLDER_NAME;
import static com.example.project_myfit.MyFitConstant.IS_PARENT_NAME;

public class FolderNameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private FolderNameEditConfirmListener mListener;

    @NotNull
    public static FolderNameEditDialog getInstance(String folderName, boolean isParentName) {
        FolderNameEditDialog folderNameEditDialog = new FolderNameEditDialog();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER_NAME, folderName);
        bundle.putBoolean(IS_PARENT_NAME, isParentName);
        folderNameEditDialog.setArguments(bundle);
        return folderNameEditDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (FolderNameEditConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String folderName = null;
        boolean isParentName = false;
        if (getArguments() != null) {
            folderName = getArguments().getString(FOLDER_NAME);
            isParentName = getArguments().getBoolean(IS_PARENT_NAME);
        }
        if (savedInstanceState != null) folderName = savedInstanceState.getString(FOLDER_NAME);

        mBinding = DialogUtils.getBinding(getLayoutInflater(), requireContext(), folderName, FOLDER);

        boolean finalIsParentName = isParentName;
        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.edit_folder_name), mBinding,
                (dialog, which) -> mListener.folderNameEditConfirmClick(String.valueOf(mBinding.dialogEditText.getText()).trim(), finalIsParentName));
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FOLDER_NAME, String.valueOf(mBinding.dialogEditText.getText()));
    }

    public interface FolderNameEditConfirmListener {
        void folderNameEditConfirmClick(String folderName, boolean isParentName);
    }
}
