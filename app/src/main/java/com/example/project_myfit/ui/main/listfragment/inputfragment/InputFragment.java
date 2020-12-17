package com.example.project_myfit.ui.main.listfragment.inputfragment;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentInputBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

public class InputFragment extends Fragment {
    public static final String TAG = "로그";
    public static final int REQUEST_CODE = 1000;
    private InputViewModel mModel;
    private FragmentInputBinding mBinding;
    private OnBackPressedCallback mCallback;
    private MainActivityViewModel mActivityModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //바인딩
        mBinding = FragmentInputBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //독립 메뉴
        setHasOptionsMenu(true);
        //뷰모델
        mModel = new ViewModelProvider(this).get(InputViewModel.class);
        //메인 액티비티 뷰모델
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //타이틀 설정
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(mActivityModel.getChildCategory().getChildCategory());
        } else {
            Log.d(TAG, "onViewCreated: 액션바 null");
        }
        //OnBackPressedCallBack 정의
        mCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //싹다 비어있으면
                if (TextUtils.isEmpty(mBinding.brand.getText())
                        && TextUtils.isEmpty(mBinding.name.getText())
                        && TextUtils.isEmpty(mBinding.size.getText())
                        && TextUtils.isEmpty(mBinding.length.getText())
                        && TextUtils.isEmpty(mBinding.shoulder.getText())
                        && TextUtils.isEmpty(mBinding.chest.getText())
                        && TextUtils.isEmpty(mBinding.sleeve.getText())
                        && TextUtils.isEmpty(mBinding.link.getText())
                        && TextUtils.isEmpty(mBinding.memo.getText())
                        && !mBinding.checkboxFavorite.isChecked()
                ) {
                    //뒤로
                    goListFragment();
                } else {
                    //하나라도 입력값이 있으면
                    showAlertDialog();
                }
            }
        };

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //add 아이콘 클릭
        mBinding.addIcon.setOnClickListener(v -> {
            getImageUri();
            v.setVisibility(View.GONE);
        });
        //이미지 클릭
        mBinding.image.setOnClickListener(v -> {
            getImageUri();
        });

    }

    //암시적 인텐트 이미지
    private void getImageUri() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Toast.makeText(requireContext(), "asdasd", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mBinding.image.setImageURI(data.getData());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //OnBackPressedCallBack 등록
        requireActivity().getOnBackPressedDispatcher().addCallback(mCallback);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //OnBackPressedCallBack 해제
        mCallback.remove();
    }

    //메뉴 인플레이트
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.inputfragment_menu, menu);
    }

    //메뉴 클릭
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //ADD 버튼 클릭
        if (item.getItemId() == R.id.menu_input_fragment_save) {
            //인서트
            setSize();
            //뒤로가기
            goListFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TopSize 인서트
    private void setSize() {
        Map<String, String> dataMap = new HashMap<>();
        Map<String, String> sizeMap = new HashMap<>();
        Map<String, String> etcMap = new HashMap<>();
        boolean isFavorite = false;
        if (mBinding.brand.getText() != null) {
            //브랜드
            dataMap.put(Size.BRAND, mBinding.brand.getText().toString());
        }
        if (mBinding.name.getText() != null) {
            //제품명
            dataMap.put(Size.NAME, mBinding.name.getText().toString());
        }
        if (mBinding.size.getText() != null) {
            //사이즈
            dataMap.put(Size.SIZE, mBinding.size.getText().toString());
        }
        if (mBinding.length.getText() != null) {
            //총장
            sizeMap.put(Size.LENGTH, mBinding.length.getText().toString());
        }
        if (mBinding.shoulder.getText() != null) {
            //어깨
            sizeMap.put(Size.SHOULDER, mBinding.shoulder.getText().toString());
        }
        if (mBinding.chest.getText() != null) {
            //가슴
            sizeMap.put(Size.CHEST, mBinding.chest.getText().toString());
        }
        if (mBinding.sleeve.getText() != null) {
            //소매
            sizeMap.put(Size.SLEEVE, mBinding.sleeve.getText().toString());
        }
        if (mBinding.link.getText() != null) {
            //링크
            etcMap.put(Size.LINK, mBinding.link.getText().toString());
        }
        if (mBinding.memo.getText() != null) {
            //메모
            etcMap.put(Size.MEMO, mBinding.memo.getText().toString());
        }
        if (mBinding.checkboxFavorite.isChecked()) {
            //즐겨찾기
            isFavorite = true;
        }
        //인서트
        mModel.SizeInsert(new Size(dataMap, sizeMap, etcMap, isFavorite, mActivityModel.getChildCategory().getId()));
    }

    //다이얼로그
    private void showAlertDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setTitle("경고");
        builder.setMessage("저장되지 않았습니다.\n 취소하시겠습니까?");
        builder.setPositiveButton("확인", (dialog, which) -> goListFragment());
        builder.setNegativeButton("취소", null);
        builder.show();
    }


    //ListFragment로 이동
    private void goListFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_inputFragment_to_listFragment);
    }

}