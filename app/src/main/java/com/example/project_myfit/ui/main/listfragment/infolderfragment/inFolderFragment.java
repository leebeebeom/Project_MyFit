package com.example.project_myfit.ui.main.listfragment.infolderfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_myfit.databinding.FragmentListBinding;

import org.jetbrains.annotations.NotNull;

public class inFolderFragment extends Fragment {

    private FragmentListBinding mBinding;

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = FragmentListBinding.inflate(inflater);
        return mBinding.getRoot();
    }
}
