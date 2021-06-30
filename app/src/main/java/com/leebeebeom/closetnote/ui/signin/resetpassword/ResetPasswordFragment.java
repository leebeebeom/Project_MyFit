package com.leebeebeom.closetnote.ui.signin.resetpassword;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentResetPasswordBinding;
import com.leebeebeom.closetnote.ui.signin.BaseSignInFragment;
import com.leebeebeom.closetnote.util.auth.AuthUtil;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.EditTextErrorKeyListener;
import com.leebeebeom.closetnote.util.ToastUtil;
import com.leebeebeom.closetnote.util.auth.Verification;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.leebeebeom.closetnote.ui.MainActivity.TAG;

@AndroidEntryPoint
public class ResetPasswordFragment extends BaseSignInFragment implements Verification.PasswordResetEmailListener {
    @Inject
    AuthUtil mAuthUtil;
    @Inject
    ToastUtil mToastUtil;
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

    public void sendPasswordResetEmail() {
        mAuthUtil.sendPasswordResetEmail(mModel.getEmail(), this);
    }

    public void setEmailEmptyError() {
        mBinding.emailLayout.setError(getString(R.string.sign_in_email_is_empty));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void sendPasswordResetEmailSuccess() {
        CommonUtil.navigate(mNavController, R.id.resetPasswordFragment, ResetPasswordFragmentDirections.toSendResetPasswordEmailFragment(mModel.getEmail()));
    }

    @Override
    public void sendPasswordResetEmailFail(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException)
            mBinding.emailLayout.setError(getString(R.string.sgin_up_email_is_badly_formatted));
        else if (e instanceof FirebaseAuthInvalidUserException)
            mBinding.emailLayout.setError(getString(R.string.reset_password_not_exsist_email));
        else mToastUtil.showUnknownError();
        Log.d(TAG, "ResetPasswordFragment : fail: " + e);
    }
}