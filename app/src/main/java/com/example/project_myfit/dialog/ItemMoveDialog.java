package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

public class ItemMoveDialog extends DialogFragment {
    public interface ItemMoveConfirmClick {
        void itemMoveConfirmClick(long folderId);
    }

    private ItemMoveConfirmClick mListener;

    public ItemMoveDialog() {
    }

    public static ItemMoveDialog getInstance(int itemAmount, long id) {
        ItemMoveDialog itemMoveDialog = new ItemMoveDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("amount", itemAmount);
        bundle.putLong("id", id);
        itemMoveDialog.setArguments(bundle);
        return itemMoveDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (ItemMoveConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int amount = 0;
        long id = 0;
        if (getArguments() != null) {
            amount = getArguments().getInt("amount");
            id = getArguments().getLong("id");
        }
        long folderId = id;
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(amount + "개의 아이템을 이동하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog1, which) -> mListener.itemMoveConfirmClick(folderId))
                .show();

        float size = getResources().getDimensionPixelSize(R.dimen._4sdp);
        float titleSize = getResources().getDimension(R.dimen._5sdp);
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        int titleId = getResources().getIdentifier("alertTitle", "id", requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);
        MaterialTextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            int padding = (int) getResources().getDimension(R.dimen._8sdp);
            message.setPadding(message.getPaddingLeft(), padding, message.getPaddingRight(), message.getPaddingBottom());
            message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
        return dialog;
    }
}
