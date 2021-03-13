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

import static com.example.project_myfit.MyFitConstant.IS_TREE_VIEW;

//서치뷰 - 트리뷰 폴더 추가시 현재 검색어가 들어가있는 아이템이라면
//액션모드 활성화 안됨
//서치뷰 - 트리뷰 폴더 추가시 서치뷰에 현재 표기되고있는 아이템 갯수 변경 안됨
public class AddFolderDialog extends DialogFragment {
    private AddFolderConfirmClick mListener;
    private TreeAddFolderConfirmClick mListener2;
    private boolean isTreeView;

    public AddFolderDialog() {
    }

    @NotNull
    public static AddFolderDialog getInstance(boolean isTreeView) {
        AddFolderDialog addFolderDialog = new AddFolderDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_TREE_VIEW, isTreeView);
        addFolderDialog.setArguments(bundle);
        return addFolderDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (getArguments() != null) isTreeView = getArguments().getBoolean(IS_TREE_VIEW);
        if (!isTreeView) mListener = (AddFolderConfirmClick) getTargetFragment();
        else mListener2 = (TreeAddFolderConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ItemDialogEditTextBinding binding = DialogUtils.getFolderBinding(getLayoutInflater(), requireContext(), null);

        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.add_folder), binding, (dialog, which) -> {
            if (!isTreeView)
                mListener.addFolderConfirmClick(String.valueOf(binding.dialogEditText.getText()));
            else
                mListener2.treeAddFolderConfirmClick(String.valueOf(binding.dialogEditText.getText()));
        });
    }

    public interface AddFolderConfirmClick {
        void addFolderConfirmClick(String folderName);
    }

    public interface TreeAddFolderConfirmClick {
        void treeAddFolderConfirmClick(String folderName);
    }
}
