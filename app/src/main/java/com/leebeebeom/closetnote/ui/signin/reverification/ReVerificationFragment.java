package com.leebeebeom.closetnote.ui.signin.reverification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentReVerificationBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReVerificationFragment extends BaseFragment {
    @Inject
    FirebaseAuth mAuth;
    @Inject
    ActionCodeSettings mActionCodeSettings;
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
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            showIndicator();
            String email = mAuth.getCurrentUser().getEmail();
            openBrowser(CommonUtil.getDomain(email));
            hideIndicator();
        }
    }

    public void reSend() {
        if (mAuth.getCurrentUser() != null) {
            showIndicator();
            mAuth.getCurrentUser().sendEmailVerification(mActionCodeSettings);
            Toast.makeText(requireContext(), R.string.re_verification_re_send_mail, Toast.LENGTH_SHORT).show();
            hideIndicator();
        }
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }
}