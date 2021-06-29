package com.leebeebeom.closetnote.ui.signin.reverification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.ActionCodeSettings;
import com.leebeebeom.closetnote.databinding.FragmentReVerificationBinding;
import com.leebeebeom.closetnote.ui.signin.BaseSignInFragment;
import com.leebeebeom.closetnote.util.AuthUtil;
import com.leebeebeom.closetnote.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReVerificationFragment extends BaseSignInFragment {
    @Inject
    AuthUtil mAuthUtil;
    @Inject
    ActionCodeSettings mActionCodeSettings;
    @Inject
    ToastUtil mToastUtil;
    private FragmentReVerificationBinding mBinding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        removeAppBar();
        removeBottomAppBar();

        mBinding = FragmentReVerificationBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        return mBinding.getRoot();
    }

    public void openEmail() {
        String email = mAuthUtil.getEmail();
        mAuthUtil.openEmail(email);
    }

    public void reSend() {
        mAuthUtil.reSendVerificationEmail();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}