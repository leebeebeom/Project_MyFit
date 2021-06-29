package com.leebeebeom.closetnote.ui.signin.resetpassword.sendrestapsswordemail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leebeebeom.closetnote.databinding.FragmentSendResetPasswordEmailBinding;
import com.leebeebeom.closetnote.ui.signin.BaseSignInFragment;
import com.leebeebeom.closetnote.util.AuthUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class SendResetPasswordEmailFragment extends BaseSignInFragment {
    @Inject
    AuthUtil mAuthUtil;
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
        mAuthUtil.openEmail(email);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}