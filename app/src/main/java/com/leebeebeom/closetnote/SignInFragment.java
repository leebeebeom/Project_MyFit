package com.leebeebeom.closetnote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.databinding.FragmentSignInBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends BaseFragment {
    @Inject
    ActivityMainBinding mActivityBinding;
    private SignInViewModel mModel;
    private FragmentSignInBinding mBinding;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(SignInViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSignInBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityBinding.fab.setVisibility(View.GONE);
        mActivityBinding.bottomAppBar.setVisibility(View.GONE);
        mActivityBinding.actionBar.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivityBinding.actionBar.getRoot().setVisibility(View.VISIBLE);
        mBinding = null;
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }
}