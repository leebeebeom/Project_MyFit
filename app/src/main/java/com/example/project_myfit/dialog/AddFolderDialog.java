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

public class AddFolderDialog extends DialogFragment {
    private AddFolderConfirmListener mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (AddFolderConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ItemDialogEditTextBinding binding = DialogUtils.getBinding(getLayoutInflater(), requireContext(), null, FOLDER);

        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.add_folder), binding, (dialog, which) ->
                mListener.addFolderConfirmClick(String.valueOf(binding.dialogEditText.getText()).trim()));
    }

    public interface AddFolderConfirmListener {
        void addFolderConfirmClick(String folderName);
    }
}
