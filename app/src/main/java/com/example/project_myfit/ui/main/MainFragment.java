package com.example.project_myfit.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    public static final String TAG = "로그";
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private RecyclerView.Adapter mAdapter;
    private CategoryAdapter adapter;


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
        //뷰모델
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        //메인 액티비티 뷰모델
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //프래그먼트 독립 메뉴 사용
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mBinding.recyclerView.addItemDecoration(decoration);
        List<ChildCategory> childCategories = new ArrayList<>();
        adapter = new CategoryAdapter(mModel.getData(childCategories));
        mBinding.recyclerView.setAdapter(adapter);
        mModel.getAll().observe(getViewLifecycleOwner(), childCategoryList -> {
            adapter = new CategoryAdapter(mModel.getData(childCategoryList));
            adapter.setOnItemClickListener(this::showDialog);
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: 호출");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: 호출");
        //최초 실행일 시 실행
        if (mModel.getRunCount() == 0) {
            mModel.setRunCount();
        }
    }

    //옵션 메뉴 인플레이트
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment_menu, menu);
    }

    //옵션 메뉴 클릭
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //ADD 메뉴 클릭시 CategoryActivity로 이동
        if (item.getItemId() == R.id.add_category) {
            Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_addCategoryActivity);
            return true;
        }
        //순서 메뉴 클릭시 정렬 다이얼로그 생성
        else if (item.getItemId() == R.id.main_fragment_order) {
            showAlertDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //정렬 다이얼로그 생성
    public void showAlertDialog() {
        SharedPreferences.Editor editor = mModel.getPreferences_sort().edit();
        //리스트 아이템
        String[] items = {"생성 날짜 순", "생성 날짜 역순", "이름 순", "이름 역순"};
        //다이얼로그 생성시 체크된 순서
        int checkedItem = mModel.getPreferences_sort().getInt(MainViewModel.SORT, 0);
        //다이얼로그 생성
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        //타이틀
        builder.setTitle("정렬 순서");
        //리스트 아이템
        builder.setSingleChoiceItems(items, checkedItem, (dialog, which) -> {
            if (which == 0) {
                //생성 순
                editor.putInt(MainViewModel.SORT, MainViewModel.ID_ASC);
                mModel.setSortInt(MainViewModel.ID_ASC);
            } else if (which == 1) {
                //생성 역순
                editor.putInt(MainViewModel.SORT, MainViewModel.ID_DESC);
                mModel.setSortInt(MainViewModel.ID_DESC);
            } else if (which == 2) {
                //이름 순
                editor.putInt(MainViewModel.SORT, MainViewModel.NAME_ASC);
                mModel.setSortInt(MainViewModel.NAME_ASC);
            } else {
                //이름 역순
                editor.putInt(MainViewModel.SORT, MainViewModel.NAME_DESC);
                mModel.setSortInt(MainViewModel.NAME_DESC);
            }
        });
        //확인 버튼
        builder.setPositiveButton("확인", (dialog, which) -> {
            //확인 누르면 프리퍼런스 저장
            editor.apply();
//            mModel.getSort().observe(getViewLifecycleOwner(), clothing -> mBinding.recyclerView.setAdapter(mModel.setAdapterItem(clothing)));
        });
        //취소 버튼
        builder.setNeutralButton("취소", null).show();
    }

    //자식 카테고리 추가 다이얼로그
    private void showDialog(ParentCategory parentCategory) {
        FrameLayout frameLayout = (FrameLayout) View.inflate(requireContext(), R.layout.item_dialog_edit_text, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog);
        builder.setView(frameLayout);
        builder.setTitle("Add Category");
        builder.setPositiveButton("확인", (dialog, which) -> {
            TextInputEditText editText = frameLayout.findViewById(R.id.editText_dialog);
            if (!TextUtils.isEmpty(editText.getText())) {
                mModel.insert(new ChildCategory(editText.getText().toString(), parentCategory.getTitle()));
            }
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

}