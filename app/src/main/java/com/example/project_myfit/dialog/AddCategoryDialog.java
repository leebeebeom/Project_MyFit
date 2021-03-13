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

//TODO 생명주기 안 코드들 메소드 화
//TODO 변수 인자로 받기
//반복 코드들 유틸화
//트리뷰 롱클릭 삭제, 이름변경
public class AddCategoryDialog extends DialogFragment {
    private AddCategoryConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (AddCategoryConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ItemDialogEditTextBinding binding = DialogUtils.getCategoryBinding(getLayoutInflater(), requireContext(), null);

        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.add_category), binding,
                (dialog, which) -> mListener.addCategoryConfirmClick(String.valueOf(binding.dialogEditText.getText())));
    }

    public interface AddCategoryConfirmClick {
        void addCategoryConfirmClick(String categoryName);
    }
}
