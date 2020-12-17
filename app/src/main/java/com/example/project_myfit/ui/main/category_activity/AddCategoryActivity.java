package com.example.project_myfit.ui.main.category_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActivityAddCategoryBinding;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {
    private AddCategoryViewModel mModel;
    private final StringBuilder mStringBuilder = new StringBuilder();
    private String mExistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //뷰모델
        mModel = new ViewModelProvider(this).get(AddCategoryViewModel.class);
        //바인딩
        ActivityAddCategoryBinding binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());

        //리사이클러뷰 셋팅(고정 데이터라 ViewModel 내부 어댑터)
        binding.recyclerview.setAdapter(mModel.getAdapter());
        setContentView(binding.getRoot());

        //액션모드 활성화
        ActionMode actionMode = startSupportActionMode(callback);
        if (actionMode != null) {
            actionMode.setTitle("Add Category");
        }

        // db에 저장된 아이템 리스트 셋팅(비동기라서 onCreate에서 미리 초기화)
        mModel.getAllClothingNotLive();
    }

    //액션 모드 콜백 정의
    private final ActionMode.Callback callback = new ActionMode.Callback() {
        //메뉴 인플레이트
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.add_category_activity_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        //메뉴 클릭 시
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //DONE 클릭 시
            if (item.getItemId() == R.id.menu_done) {
                List<ChildCategory> childCategoryList = mModel.getAdapter().getSelectedItem(); // 선택된 아이템 리스트(어댑터에서)
                List<ChildCategory> dbChildCategoryList = mModel.getChildCategoryListNotLive(); // db에 저장된 리스트
                List<String> dBClothingListString = new ArrayList<>(); // 빈 String 리스트
                //선택된 아이템 리스트의 스트링과 db에 저장된 스트링 전체를 비교하기 위해서 내용물을 스트링 리스트에 담아줌
                for (ChildCategory childCategory : dbChildCategoryList) {
                    dBClothingListString.add(childCategory.getChildCategory());
                    //dBClothingListString 리스트에 db에 저장된 카테고리들이 들어있음
                }
                for (ChildCategory childCategory : childCategoryList) {
                    //선택된 아이템 갯수 만큼 db에 저장되어 있는지 확인
                    if (dBClothingListString.contains(childCategory.getChildCategory())) {
                        //있다면?
                        mStringBuilder.append(childCategory.getChildCategory()).append("\n");
                        //스트링 빌더에 담아줌
                    }
                }
                //스트링 빌더에 담긴 리스트를 mEixistList 에 담아줌
                mExistList = mStringBuilder.toString();
                for (ChildCategory childCategory : childCategoryList) {
                    //있다면?
                    if (dBClothingListString.contains(childCategory.getChildCategory())) {
                        //다이얼로그 프래그먼트
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddCategoryActivity.this, R.style.myAlertDialog);
                        builder.setTitle("경고");
                        builder.setMessage(mExistList + "카테고리가 이미 존재합니다.\n그래도 추가하시겠습니까?");
                        builder.setPositiveButton("확인", (dialog, which) -> {
                            for (ChildCategory childCategoryYes : childCategoryList) {
                                //확인 클릭 시 인서트
                                mModel.insert(childCategoryYes);
                                //액티비티 종료
                                finish();
                            }
                        });
                        builder.setNegativeButton("취소", (dialog, which) -> {
                            //취소 클릭 시 스트링 빌더 초기화
                            mStringBuilder.delete(0, mStringBuilder.length());
                            //ExistList 초기화
                            mExistList = "";
                        });
                        builder.show();
                        //포문 스탑, 클릭 함수 종료
                        return true;
                    }
                }
                //없다면?
                for (ChildCategory childCategory : childCategoryList) {
                    mModel.insert(childCategory);
                }
                finish();
            }
            return false;
        }
        
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //액션모드 종료되면 액티비티도 종료
            finish();
        }
    };
}