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
import com.example.project_myfit.util.CommonUtil;

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
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, R.id.nav_graph_main).backStackLiveSetValue(R.id.addDialog);

        ItemDialogEditTextBinding binding = dialogUtil.getBinding(null, mItemType);

        AlertDialog alertDialog = getDialog(dialogUtil, binding);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveClick(dialogUtil, binding, positiveButton);
        dialogUtil.imeClick(binding, positiveButton);
        return alertDialog;
    }

    @NotNull
    private AlertDialog getDialog(DialogUtil dialogUtil, ItemDialogEditTextBinding binding) {
        if (mItemType.equals(CATEGORY))
            return dialogUtil.getEditTextDialog(binding, getString(R.string.all_add_category), null);
        else
            return dialogUtil.getEditTextDialog(binding, getString(R.string.all_create_folder), null);
    }

    private void positiveClick(DialogUtil dialogUtil, ItemDialogEditTextBinding binding, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            CommonUtil.keyBoardHide(requireContext(), v);

            String newName = String.valueOf(binding.et.getText()).trim();

            if (mItemType.equals(CATEGORY))
                dialogUtil.addCategory(newName, mParentCategory, false);
            else dialogUtil.addFolder(newName, mParentId, mParentCategory, false);
        });
    }
}
