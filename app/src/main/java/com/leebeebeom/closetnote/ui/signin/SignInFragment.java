package com.leebeebeom.closetnote.ui.signin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentSignInBinding;
import com.leebeebeom.closetnote.di.Qualifiers;
import com.leebeebeom.closetnote.util.AuthUtil;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.EditTextErrorKeyListener;
import com.leebeebeom.closetnote.util.ToastUtil;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.leebeebeom.closetnote.ui.MainActivity.TAG;

@AndroidEntryPoint
public class SignInFragment extends BaseSignInFragment implements AuthUtil.EmailSignInListener {
    @Inject
    NavController mNavController;
    @Qualifiers.GoogleSignInIntent
    @Inject
    Intent mGoogleSignInIntent;
    @Inject
    ToastUtil mToastUtil;
    @Inject
    AuthUtil mAuthUtil;

    private SignInViewModel mModel;
    private FragmentSignInBinding mBinding;
    private String mNickname, mProfileUrl;
    private final ActivityResultLauncher<Intent> mGoogleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            mAuthUtil.signInWithGoogle(account);
        } catch (ApiException e) {
            mToastUtil.showGoogleSignInFail();
            Log.d(TAG, "SignInFragment: mGoogleSignInLauncher: ApiException" + e.getMessage());
            hideIndicator();
        }
    });

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(SignInViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        removeBottomAppBar();
        removeAppBar();

        mBinding = FragmentSignInBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        mBinding.setModel(mModel);
        mBinding.setLifecycleOwner(this);

        mBinding.et.etEmail.addTextChangedListener(new EditTextErrorKeyListener(mBinding.et.emailLayout));
        mBinding.et.etPassword.addTextChangedListener(new EditTextErrorKeyListener(mBinding.et.passwordLayout));
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void setEmptyError() {
        if (mModel.isEmailEmpty())
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_email_is_empty));
        if (mModel.isPasswordEmpty())
            mBinding.et.passwordLayout.setError(getString(R.string.sign_in_password_is_empty));
    }

    public void emailSignIn() {
        mAuthUtil.emailSignIn(mModel.getEmail(), mModel.getPassword(), this);
    }

    public void googleSignIn() {
        mGoogleSignInLauncher.launch(mGoogleSignInIntent);
    }

    @SuppressLint("HandlerLeak")
    public void naverSignIn() {
        mAuthUtil.naverSignIn();
    }

    public void kakaoSignIn() {
        mAuthUtil.kakaoSignIn();
    }

    public void navigateSignUpFragment() {
        CommonUtil.navigate(mNavController, R.id.signInFragment, SignInFragmentDirections.toSignUpFragment());
    }

    public void navigateResetPasswordFragment() {
        CommonUtil.navigate(mNavController, R.id.signInFragment, SignInFragmentDirections.toResetPasswordFragment());
    }

    public void signInAnonymously() {
        mAuthUtil.signInAnonymously();
    }

    @Override
    public void emailSignInNotVerification() {
        CommonUtil.navigate(mNavController, R.id.signInFragment, SignInFragmentDirections.toReVerificationFragment());
    }

    @Override
    public void emailSignInFail(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_check_email_or_password));
            mBinding.et.passwordLayout.setError(getString(R.string.sign_in_check_email_or_password));
        } else if (e instanceof FirebaseAuthInvalidUserException)
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_not_exist_email));
        else mToastUtil.showUnknownError();
        Log.d(TAG, "SignInFragment: emailSignInFail: " + e);
    }
}