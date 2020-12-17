package com.example.project_myfit.ui.main.listfragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //바인딩
        mBinding = FragmentListBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //뷰모델
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
        //액티비티 뷰모델
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //타이틀 설정
        //캐스팅 안해주면 NPE
        //캐스팅 해야 SupportActionBar 가져올 수 있음
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(mActivityModel.getChildCategory().getChildCategory());
        }
        //독립 옵션 메뉴
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //어댑터 셋팅
        mModel.getSizeList(mActivityModel.getChildCategory().getId()).observe(getViewLifecycleOwner(), Sizes -> {
            mBinding.listFragmentRecyclerView.setAdapter(mModel.setAdapterItem(Sizes));
            mModel.getAdapter().setOnItemClickedListener(size -> {
                //클릭된 size 아이템 MainActivityViewModel에 저장
                mActivityModel.setSize(size);
                //OutputFragment ㄱㄱ
                Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_outputFragment);
                //TODO
                //클릭 리스너
            });
        });

    }

    //옵션 메뉴 인플레이트
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.listfragment_menu, menu);
    }

    //메뉴 클릭
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_item) {
            goInputFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goInputFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputFragment);
    }
}
//TODO
//item_list_fragment 완성 해야됨
//폴더 추가