package com.leebeebeom.closetnote.ui.signin.sendrestapsswordemail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.leebeebeom.closetnote.databinding.FragmentSendResetPasswordEmailBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SendResetPasswordEmailFragment extends BaseFragment {
    @Inject
    FirebaseAuth mAuth;
    private FragmentSendResetPasswordEmailBinding mBinding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        removeAppBar();
        removeBottomAppBar();

        mBinding = FragmentSendResetPasswordEmailBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        return mBinding.getRoot();
    }

    public void openMail() {
        String email = SendResetPasswordEmailFragmentArgs.fromBundle(getArguments()).getEmail();
        openBrowser(CommonUtil.getDomain(email));
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }
}