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

//테스트 끝

//TODO 셀렉트 포지션 포지션 -> 아이디로

//TODO 트리뷰 나올때 리스트뷰랑 같은 순서로 나오게
//TODO 트리뷰 폴더 추가시 리스트뷰랑 같은 순서로 추가되게(가능?)

//TODO 서치뷰 순서 체크
//TODO 트리뷰 나올때 서치뷰랑 같은 순서로 나오게
//TODO 트리뷰 폴더 추가시 서치뷰랑 같은 순서로 추가되게(가능?)

//TODO 엔드아이콘 클릭하면 키보드 보이게

//테스트 끝
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
