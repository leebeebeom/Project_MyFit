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

    private int mItemType, mNavGraphId;
    private String mParentCategory;
    private long mParentId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = AddDialogArgs.fromBundle(getArguments()).getItemType();
        mParentCategory = AddDialogArgs.fromBundle(getArguments()).getParentCategory();
        mParentId = AddDialogArgs.fromBundle(getArguments()).getParentId();
        mNavGraphId = AddDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId).setValueBackStackLive(R.id.addDialog);

        ItemDialogEditTextBinding binding = mItemType == CATEGORY ? dialogUtil.getCategoryBinding() : dialogUtil.getFolderBinding();
        AlertDialog alertDialog = mItemType == CATEGORY ?
                dialogUtil.getAddDialog(binding, getString(R.string.all_add_category)) : dialogUtil.getAddDialog(binding, getString(R.string.all_create_folder));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveClick(dialogUtil, binding, positiveButton);
        dialogUtil.imeClick(binding, positiveButton);
        return alertDialog;
    }

    private void positiveClick(DialogUtil dialogUtil, ItemDialogEditTextBinding binding, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            CommonUtil.keyBoardHide(requireContext(), v);

            String name = String.valueOf(binding.et.getText()).trim();

            if (mItemType == CATEGORY)
                dialogUtil.addCategory(name, mParentCategory, mNavGraphId);
            else dialogUtil.addFolder(name, mParentId, mParentCategory, mNavGraphId);
        });
    }
}
