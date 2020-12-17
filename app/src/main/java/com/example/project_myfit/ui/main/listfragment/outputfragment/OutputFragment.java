package com.example.project_myfit.ui.main.listfragment.outputfragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.databinding.FragmentInputBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;

public class OutputFragment extends Fragment {


    private FragmentInputBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentInputBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //액티비티 뷰모델
        MainActivityViewModel mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //데이터 셋팅
        Size size = mActivityModel.getSize();
        mBinding.brand.setText(size.getBrand());
        mBinding.name.setText(size.getName());
        mBinding.size.setText(size.getSize());
        mBinding.length.setText(size.getLength());
        mBinding.shoulder.setText(size.getShoulder());
        mBinding.chest.setText(size.getShoulder());
        mBinding.sleeve.setText(size.getSleeve());
        mBinding.link.setText(size.getLink());
        mBinding.memo.setText(size.getMemo());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}