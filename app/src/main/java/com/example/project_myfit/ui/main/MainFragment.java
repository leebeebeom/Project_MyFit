package com.example.project_myfit.ui.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainFragment extends Fragment {
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private boolean firstLoad = true;
    private int clickedPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //바인딩
        mBinding = FragmentMainBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //뷰모델
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        //메인 액티비티 뷰모델
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //프래그먼트 독립 메뉴 사용
        setHasOptionsMenu(true);
        //리사이클러 뷰 셋팅
        recyclerViewInit();
        mModel.getAll().observe(getViewLifecycleOwner(), childCategoryList -> {
            Log.d("로그", "onActivityCreated: onChanged 호출");
            //프래그먼트 생성시 한번만 셋팅(for nodeAddData)
            if (firstLoad) {
                mModel.getMainFragmentAdapter().setList(mModel.getData(childCategoryList));
                firstLoad = false;
            } else if (!firstLoad) {
                Log.d("로그", "onActivityCreated: 왜호출됨");
                //카테고리 추가시 호출
                listUpdate(childCategoryList);
            }
        });
        // + 아이콘 클릭
        mModel.getMainFragmentAdapter().setOnItemChildClickListener((adapter, view, position) -> {
            //클릭된 포지션 저장
            clickedPosition = position;
            //카테고리 생성 다이얼로그(클릭된 부모 카테고리)
            showDialog((ParentCategory) adapter.getData().get(position));
        });
    }

    //리사이클러 뷰 셋팅
    private void recyclerViewInit() {
        mBinding.recyclerView.setHasFixedSize(true);
        //디바이더
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        mBinding.recyclerView.setAdapter(mModel.getMainFragmentAdapter());
    }

    //리스트 업데이트
    private void listUpdate(List<ChildCategory> childCategoryList) {
        BaseNode parentNode = mModel.getMainFragmentAdapter().getData().get(clickedPosition);//추가될 부모 카테고리
        final int addItem = childCategoryList.size() - 1; // 추가될 아이템 인덱스(childCategoryList 마지막)
        final int childIndex = mModel.getMainFragmentAdapter().getData().get(clickedPosition).getChildNode().size(); //추가될 위치(리스트 마지막)
        mModel.getMainFragmentAdapter().nodeAddData(parentNode, childIndex, childCategoryList.get(addItem));
    }

    //자식 카테고리 추가 다이얼로그
    private void showDialog(ParentCategory parentCategory) {
        //다이얼로그에 넣을 에딧텍스트
        FrameLayout frameLayout = (FrameLayout) View.inflate(requireContext(), R.layout.item_dialog_edit_text, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setView(frameLayout);
        builder.setTitle("Add");
        builder.setPositiveButton("확인", (dialog, which) -> {
            TextInputEditText editText = frameLayout.findViewById(R.id.editText_dialog);
            if (!TextUtils.isEmpty(editText.getText())) {//텍스트가 비어있지 않다면(띄어쓰기 포함)
                //인서트(onActivityCreate observe 호출)
                mModel.insert(new ChildCategory(editText.getText().toString(), parentCategory.getParentCategory()));
            }
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }
}