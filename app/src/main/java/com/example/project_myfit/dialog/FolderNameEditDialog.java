package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.FOLDER_NAME;

public class FolderNameEditDialog extends DialogFragment {

    private ItemDialogEditTextBinding mBinding;
    private FolderNameEditConfirmClick mListener;

    public FolderNameEditDialog() {
    }

    @NotNull
    public static FolderNameEditDialog getInstance(String folderName) {
        FolderNameEditDialog folderNameEditDialog = new FolderNameEditDialog();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER_NAME, folderName);
        folderNameEditDialog.setArguments(bundle);
        return folderNameEditDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (FolderNameEditConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String folderName = null;
        if (getArguments() != null) folderName = getArguments().getString(FOLDER_NAME);
        if (savedInstanceState != null) folderName = savedInstanceState.getString(FOLDER_NAME);

        mBinding = DialogUtils.getFolderBinding(getLayoutInflater(), requireContext(), folderName);
        DialogInterface.OnClickListener listener = (dialog, which) -> mListener.folderNameEditConfirmClick(String.valueOf(mBinding.dialogEditText.getText()));
        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.edit_folder_name), mBinding, listener);
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FOLDER_NAME, String.valueOf(mBinding.dialogEditText.getText()));
    }

    public interface FolderNameEditConfirmClick {
        void folderNameEditConfirmClick(String folderName);
    }
}
