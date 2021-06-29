package com.leebeebeom.closetnote.ui.signin.signup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentSignUpBinding;
import com.leebeebeom.closetnote.ui.signin.BaseSignInFragment;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.EditTextErrorKeyListener;
import com.leebeebeom.closetnote.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.leebeebeom.closetnote.ui.MainActivity.TAG;

@AndroidEntryPoint
public class SignUpFragment extends BaseSignInFragment {
    @Inject
    FirebaseAuth mAuth;
    @Inject
    ActionCodeSettings mActionCodeSettings;
    @Inject
    ToastUtil mToastUtil;
    private SignUpViewModel mModel;
    private FragmentSignUpBinding mBinding;
    private boolean isAnonymously;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        try {
            if (mNavController.getPreviousBackStackEntry() == mNavController.getBackStackEntry(R.id.anonymouslySignUpFragment))
                isAnonymously = true;
        } catch (IllegalArgumentException e) {
            isAnonymously = false;
        }
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
        if (!isAnonymously) createUser();
        else linkWithEmail();
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(mModel.getEmail(), mModel.getPassword())
                .addOnCompleteListener(requireActivity(), getOnCompleteListener());
    }

    private void linkWithEmail() {
        if (mAuth.getCurrentUser() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(mModel.getEmail(), mModel.getPassword());
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(requireActivity(), getOnCompleteListener());
        }
    }

    @NotNull
    private OnCompleteListener<AuthResult> getOnCompleteListener() {
        return task -> {
            if (task.isSuccessful()) {
                userNameUpdate();
                sendEmailVerification();
            } else fail(task.getException());
        };
    }

    private void fail(Exception e) {
        Log.d(TAG, "SignUpFragment : fail: " + e);
        if (e instanceof FirebaseAuthWeakPasswordException)
            mBinding.et.passwordLayout.setError(getString(R.string.sign_up_password_is_less_than_6));
        else if (e instanceof FirebaseAuthInvalidCredentialsException)
            mBinding.et.emailLayout.setError(getString(R.string.sgin_up_email_is_badly_formatted));
        else if (e instanceof FirebaseAuthUserCollisionException)
            mBinding.et.emailLayout.setError(getString(R.string.sign_up_email_is_used));
        else mToastUtil.showUnknownError();
        hideIndicator();
    }

    private void userNameUpdate() {
        if (mAuth.getCurrentUser() != null)
            mAuth.getCurrentUser().updateProfile(getUserProfileChangeRequest())
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (!task.isSuccessful()) {
                            mToastUtil.showUsernameUpdateFail();
                            Log.d(TAG, "SignUpFragment : userNameUpdate: " + task.getException());
                        }
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
                        else {
                            mToastUtil.showSendVerificationMailFail();
                            Log.d(TAG, "SignUpFragment : sendEmailVerification: " + task.getException());
                        }
                        hideIndicator();
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
}