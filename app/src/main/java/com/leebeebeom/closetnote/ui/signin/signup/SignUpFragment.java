package com.leebeebeom.closetnote.ui.signin.signup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentSignUpBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.EditTextErrorKeyListener;

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
        if (mModel.isTextsNotEmpty()) {
            if (mModel.isPasswordNotEquals())
                mBinding.confirmPasswordLayout.setError(getString(R.string.sgin_in_password_not_equal));
            else {
                showIndicator();
                mAuth.createUserWithEmailAndPassword(mModel.getEmail(), mModel.getPassword())
                        .addOnFailureListener(requireActivity(), getOnFailureListener())
                        .addOnSuccessListener(requireActivity(), getOnSuccessListener())
                        .addOnCanceledListener(requireActivity(), getOnCanceledListener());
            }
        } else setEmptyError();
    }

    @NotNull
    private OnFailureListener getOnFailureListener() {
        return e -> {
            Log.d(TAG, "getOnFailureListener: " + e);
            if (e instanceof FirebaseAuthWeakPasswordException)
                mBinding.et.passwordLayout.setError(getString(R.string.sign_up_password_is_less_than_6));
            else if (e instanceof FirebaseAuthInvalidCredentialsException)
                mBinding.et.emailLayout.setError(getString(R.string.sgin_up_email_is_badly_formatted));
            else if (e instanceof FirebaseAuthUserCollisionException)
                mBinding.et.emailLayout.setError(getString(R.string.sign_up_email_is_used));
            else if (e instanceof FirebaseNetworkException)
                Toast.makeText(requireContext(), R.string.all_please_check_internet, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(requireContext(), getString(R.string.sign_up_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
            hideIndicator();
        };
    }

    @NotNull
    private OnCanceledListener getOnCanceledListener() {
        return () -> {
            Toast.makeText(requireContext(), getString(R.string.sign_up_canceled), Toast.LENGTH_SHORT).show();
            hideIndicator();
        };
    }

    @NotNull
    private OnSuccessListener<AuthResult> getOnSuccessListener() {
        return authResult -> {
            if (authResult.getUser() != null) {
                FirebaseUser user = authResult.getUser();
                userNameUpdate(user);
                sendEmailVerification(user);
            }
        };
    }

    private void userNameUpdate(FirebaseUser user) {
        user.updateProfile(getUserProfileChangeRequest())
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful())
                        Log.d(TAG, "getOnSuccessListener: 유저네임 업데이트 완료");
                    else
                        Log.d(TAG, "getOnSuccessListener: 유저네임 업데이트 실패" + task.getException());
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification(mActionCodeSettings).
                addOnSuccessListener(requireActivity(), unused -> {
                    CommonUtil.navigate(mNavController, R.id.signUpFragment, SignUpFragmentDirections.toVerificationFragment());
                    hideIndicator();
                }).
                addOnFailureListener(requireActivity(), getOnFailureListener())
                .addOnCanceledListener(requireActivity(), getOnCanceledListener());
    }

    @NotNull
    private UserProfileChangeRequest getUserProfileChangeRequest() {
        return new UserProfileChangeRequest.Builder()
                .setDisplayName(mModel.getUserName())
                .build();
    }

    private void setEmptyError() {
        if (mModel.isUsernameEmpty())
            mBinding.usernameLayout.setError(getString(R.string.sing_up_username_is_empty));
        if (mModel.isEmailEmpty())
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_email_is_empty));
        if (mModel.isPasswordEmpty())
            mBinding.et.passwordLayout.setError(getString(R.string.sign_in_password_is_empty));
        if (mModel.isConfirmPasswordEmpty())
            mBinding.confirmPasswordLayout.setError(getString(R.string.sign_in_password_is_empty));
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }
}