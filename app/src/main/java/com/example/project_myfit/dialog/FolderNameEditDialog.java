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

public class FolderNameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private FolderNameEditConfirmListener mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (FolderNameEditConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String folderName = FolderNameEditDialogArgs.fromBundle(getArguments()).getFolderName();
        boolean isParentName = FolderNameEditDialogArgs.fromBundle(getArguments()).getIsParentName();
        folderName = savedInstanceState != null ? savedInstanceState.getString(FOLDER_NAME) : folderName;

        mBinding = DialogUtils.getBinding(getLayoutInflater(), requireContext(), folderName, FOLDER);

        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.edit_folder_name), mBinding,
                (dialog, which) -> mListener.folderNameEditConfirmClick(String.valueOf(mBinding.dialogEditText.getText()).trim(), isParentName));
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
