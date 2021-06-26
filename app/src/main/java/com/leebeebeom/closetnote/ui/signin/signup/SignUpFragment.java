package com.leebeebeom.closetnote.ui.signin.signup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentSignUpBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.EditTextErrorKeyListener;
import com.leebeebeom.closetnote.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.leebeebeom.closetnote.ui.main.MainActivity.TAG;

@AndroidEntryPoint
public class SignUpFragment extends BaseFragment {
    @Inject
    FirebaseAuth mAuth;
    @Inject
    ActionCodeSettings mActionCodeSettings;
    @Inject
    ToastUtil mToastUtil;
    private SignUpViewModel mModel;
    private FragmentSignUpBinding mBinding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        removeAppBar();
        removeBottomAppBar();

        mBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        mBinding.setSignUpFragment(this);
        mBinding.setModel(mModel);
        mBinding.setLifecycleOwner(this);

        mBinding.etUsername.addTextChangedListener(new EditTextErrorKeyListener(mBinding.usernameLayout));
        mBinding.et.etEmail.addTextChangedListener(new EditTextErrorKeyListener(mBinding.et.emailLayout));
        mBinding.et.etPassword.addTextChangedListener(new EditTextErrorKeyListener(mBinding.et.passwordLayout));
        mBinding.etConfirmPassword.addTextChangedListener(new EditTextErrorKeyListener(mBinding.confirmPasswordLayout));
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void emailSignUp() {
        showIndicator();
        mAuth.createUserWithEmailAndPassword(mModel.getEmail(), mModel.getPassword())
                .addOnCompleteListener(requireActivity(), command -> {
                    if (command.isSuccessful()) {
                        userNameUpdate();
                        sendEmailVerification();
                    } else fail(command.getException());
                    hideIndicator();
                });
    }

    private void fail(Exception e) {
        Log.d(TAG, "getOnFailureListener: " + e);
        if (e instanceof FirebaseAuthWeakPasswordException)
            mBinding.et.passwordLayout.setError(getString(R.string.sign_up_password_is_less_than_6));
        else if (e instanceof FirebaseAuthInvalidCredentialsException)
            mBinding.et.emailLayout.setError(getString(R.string.sgin_up_email_is_badly_formatted));
        else if (e instanceof FirebaseAuthUserCollisionException)
            mBinding.et.emailLayout.setError(getString(R.string.sign_up_email_is_used));
        else if (e instanceof FirebaseNetworkException)
            mToastUtil.showCheckNetwork();
        else mToastUtil.showUnknownError();
    }

    private void userNameUpdate() {
        if (mAuth.getCurrentUser() != null)
            mAuth.getCurrentUser().updateProfile(getUserProfileChangeRequest())
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful())
                            Log.d(TAG, "getOnSuccessListener: 유저네임 업데이트 완료");
                        else Log.d(TAG, "getOnSuccessListener: 유저네임 업데이트 실패" + task.getException());
                    });
    }

    @NotNull
    private UserProfileChangeRequest getUserProfileChangeRequest() {
        return new UserProfileChangeRequest.Builder()
                .setDisplayName(mModel.getUserName())
                .build();
    }

    private void sendEmailVerification() {
        if (mAuth.getCurrentUser() != null)
            mAuth.getCurrentUser().sendEmailVerification(mActionCodeSettings).
                    addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful())
                            CommonUtil.navigate(mNavController, R.id.signUpFragment, SignUpFragmentDirections.toVerificationFragment());
                        else fail(task.getException());
                    });
    }

    public void setEditTextLayoutError() {
        if (mModel.isUsernameEmpty())
            mBinding.usernameLayout.setError(getString(R.string.sing_up_username_is_empty));
        if (mModel.isEmailEmpty())
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_email_is_empty));
        if (mModel.isPasswordEmpty())
            mBinding.et.passwordLayout.setError(getString(R.string.sign_in_password_is_empty));
        if (mModel.isConfirmPasswordEmpty())
            mBinding.confirmPasswordLayout.setError(getString(R.string.sign_in_password_is_empty));
        if (mModel.isPasswordEquals())
            mBinding.confirmPasswordLayout.setError(getString(R.string.sgin_in_password_not_equal));
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }
}