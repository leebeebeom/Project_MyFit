package com.leebeebeom.closetnote.ui.signin.resetpassword;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentResetPasswordBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.EditTextErrorKeyListener;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.leebeebeom.closetnote.ui.main.MainActivity.TAG;

@AndroidEntryPoint
public class ResetPasswordFragment extends BaseFragment {
    @Inject
    FirebaseAuth mAuth;
    @Inject
    ActionCodeSettings mActionCodeSettings;
    private FragmentResetPasswordBinding mBinding;
    private ResetPasswordViewModel mModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        removeAppBar();
        removeBottomAppBar();

        mBinding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        mBinding.setResetPasswordFragment(this);
        mBinding.setModel(mModel);
        mBinding.setLifecycleOwner(this);
        mBinding.etEmail.addTextChangedListener(new EditTextErrorKeyListener(mBinding.emailLayout));
        return mBinding.getRoot();
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }

    public void sendPasswordResetEmail() {
        showIndicator();
        mAuth.sendPasswordResetEmail(mModel.getEmail(), mActionCodeSettings)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful())
                        CommonUtil.navigate(mNavController, R.id.resetPasswordFragment, ResetPasswordFragmentDirections.toSendResetPasswordEmailFragment(mModel.getEmail()));
                    else fail(task.getException());
                    hideIndicator();
                });
    }

    public void setEmailEmptyError() {
        mBinding.emailLayout.setError(getString(R.string.sign_in_email_is_empty));
    }

    private void fail(Exception e) {
        Log.d(TAG, "getOnFailureListener: " + e);
        if (e instanceof FirebaseAuthInvalidCredentialsException)
            mBinding.emailLayout.setError(getString(R.string.sgin_up_email_is_badly_formatted));
        else if (e instanceof FirebaseNetworkException)
            mBinding.emailLayout.setError(getString(R.string.all_please_check_internet));
        else if (e instanceof FirebaseAuthInvalidUserException)
            mBinding.emailLayout.setError(getString(R.string.reset_password_not_exsist_email));
        else
            Toast.makeText(requireContext(), R.string.reset_password_fail, Toast.LENGTH_SHORT).show();
    }
}