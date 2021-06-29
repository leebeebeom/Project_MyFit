package com.leebeebeom.closetnote.ui.signin.signup.verification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.google.firebase.auth.FirebaseAuth;
import com.leebeebeom.closetnote.databinding.FragmentVerificationBinding;
import com.leebeebeom.closetnote.ui.signin.BaseSignInFragment;
import com.leebeebeom.closetnote.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VerificationFragment extends BaseSignInFragment {
    @Inject
    FirebaseAuth mAuth;
    @Inject
    NavController mNavController;
    private FragmentVerificationBinding mBinding;
    private String mEmail = "";

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            mEmail = mAuth.getCurrentUser().getEmail();
            mAuth.signOut();
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        removeAppBar();
        removeBottomAppBar();

        mBinding = FragmentVerificationBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void openMail() {
        showIndicator();
        openBrowser(CommonUtil.getDomain(mEmail));
        hideIndicator();
    }
}