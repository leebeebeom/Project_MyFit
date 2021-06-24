package com.leebeebeom.closetnote.ui.verification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.google.firebase.auth.FirebaseAuth;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentVerificationBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VerificationFragment extends BaseFragment {
    @Inject
    FirebaseAuth mAuth;
    @Inject
    NavController mNavController;
    private FragmentVerificationBinding mBinding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        removeAppBar();
        removeBottomAppBar();

        mBinding = FragmentVerificationBinding.inflate(inflater, container, false);
        mBinding.setVerificationFragment(this);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void openBrowser() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            int index = email.indexOf("@");
            String domain = "https://" + email.substring(index + 1);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(domain));
            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(intent);
                mNavController.popBackStack(R.id.signInFragment, true);
            }
        }
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }
}