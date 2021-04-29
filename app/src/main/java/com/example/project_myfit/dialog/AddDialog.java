package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.util.KeyboardUtil;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;

public class AddDialog extends DialogFragment {

    private String mItemType;
    private String mParentCategory;
    private long mParentId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = AddDialogArgs.fromBundle(getArguments()).getItemType();
        mParentCategory = AddDialogArgs.fromBundle(getArguments()).getParentCategory();
        mParentId = AddDialogArgs.fromBundle(getArguments()).getParentId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main).backStackLiveSetValue(R.id.addDialog);

        ItemDialogEditTextBinding binding = dialogUtils.getBinding(null, mItemType);
        AlertDialog alertDialog = getDialog(dialogUtils, binding);
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveClick(dialogUtils, binding, positiveButton);
        dialogUtils.imeClick(binding, positiveButton);
        return alertDialog;
    }

    @NotNull
    private AlertDialog getDialog(DialogUtils dialogUtils, ItemDialogEditTextBinding binding) {
        AlertDialog alertDialog;
        if (mItemType.equals(CATEGORY))
            alertDialog = dialogUtils.getEditTextDialog(binding, getString(R.string.all_add_category), null);
        else
            alertDialog = dialogUtils.getEditTextDialog(binding, getString(R.string.all_create_folder), null);
        return alertDialog;
    }

    private void positiveClick(DialogUtils dialogUtils, ItemDialogEditTextBinding binding, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            KeyboardUtil.hide(requireContext(), v);

            String newName = String.valueOf(binding.etDialog.getText()).trim();

            if (mItemType.equals(CATEGORY))
                dialogUtils.addCategory(newName, mParentCategory, false);
            else dialogUtils.addFolder(newName, mParentId, mParentCategory, false);
        });
    }
}
