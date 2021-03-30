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

//TODO 디자인 패턴은 나중에

//TODO 생명주기 안 코드들 메소드 화
//TODO 코드 최대한 간결하고 직관적으로
//TODO 변수 인자로 받기

//TODO 반복 코드들 유틸화
//TODO 빌더 패턴, 팩토리 패턴, 추상 팩토리 패턴 공부
//TODO 트리뷰 롱클릭으로 삭제, 이름변경
//TODO 탭레이아웃 뱃지 텍스트 사이즈

//TODO 구현 못한 것
//TODO 폴더 이름, 카테고리 이름 변경 시 분신술
//TODO 트리뷰 카테고리 expand 유지

/*
테스트 (완료)
다이얼로그 생성, 로테이트, 30자 제한, 공백
 */
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
