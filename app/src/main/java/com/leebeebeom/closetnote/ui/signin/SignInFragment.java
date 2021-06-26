package com.leebeebeom.closetnote.ui.signin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.User;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentSignInBinding;
import com.leebeebeom.closetnote.di.Qualifiers;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.EditTextErrorKeyListener;
import com.leebeebeom.closetnote.util.ToastUtil;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static com.leebeebeom.closetnote.ui.main.MainActivity.TAG;

@AndroidEntryPoint
public class SignInFragment extends BaseFragment {
    @Inject
    NavController mNavController;
    @Inject
    FirebaseAuth mAuth;
    @Inject
    OAuthLogin mOAuthLogin;
    @Inject
    ActionCodeSettings mActionCodeSettings;
    @Qualifiers.GoogleSignInIntent
    @Inject
    Intent mGoogleSignInIntent;
    @Inject
    UserApiClient mUserApiClient;
    @Inject
    ToastUtil mToastUtil;

    private SignInViewModel mModel;
    private FragmentSignInBinding mBinding;
    private String mNickname, mProfileUrl;
    private final ActivityResultLauncher<Intent> mGoogleLoginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        showIndicator();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            mToastUtil.showGoogleSignInFail();
            Log.d(TAG, "mGoogleLoginLauncher: ApiException" + e.getMessage());
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
        mBinding.setSignInFragment(this);
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

    public void emailSignIn() {
        showIndicator();
        mAuth.signInWithEmailAndPassword(mModel.getEmail(), mModel.getPassword())
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) emailSignInSuccess();
                    else emailSignInFail(task.getException());
                    hideIndicator();
                });
    }

    private void emailSignInSuccess() {
        if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isEmailVerified())
            CommonUtil.navigate(mNavController, R.id.signInFragment, SignInFragmentDirections.toReVerificationFragment());
        else mNavController.popBackStack();
    }

    private void emailSignInFail(Exception e) {
        Log.d(TAG, "fail: " + e);
        if (e instanceof FirebaseNetworkException)
            mToastUtil.showCheckNetwork();
        else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_check_email_or_password));
            mBinding.et.passwordLayout.setError(getString(R.string.sign_in_check_email_or_password));
        } else if (e instanceof FirebaseAuthInvalidUserException)
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_not_exist_email));
    }

    public void setEmptyError() {
        if (mModel.isEmailEmpty())
            mBinding.et.emailLayout.setError(getString(R.string.sign_in_email_is_empty));
        if (mModel.isPasswordEmpty())
            mBinding.et.passwordLayout.setError(getString(R.string.sign_in_password_is_empty));
    }

    public void googleSignIn() {
        showIndicator();
        mGoogleLoginLauncher.launch(mGoogleSignInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        if (account != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            mToastUtil.showGoogleSignInSuccess();
                            mNavController.popBackStack();
                        } else {
                            mToastUtil.showGoogleSignInFail();
                            Log.d(TAG, "firebaseAuthWithGoogle: !task.isSuccessful" + task.getException());
                        }
                        hideIndicator();
                    });
        }
    }

    @SuppressLint("HandlerLeak")
    public void naverSignIn() {
        showIndicator();
        mOAuthLogin.startOauthLoginActivity(requireActivity(), new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success)
                    new Thread(() -> {
                        JsonElement userInfo = getNaverUserInfo();
                        String email = "naver_" + userInfo.getAsJsonObject().get("email").getAsString();
                        String id = userInfo.getAsJsonObject().get("id").getAsString();
                        mNickname = userInfo.getAsJsonObject().get("nickname").getAsString();
                        mProfileUrl = userInfo.getAsJsonObject().get("profile_image").getAsString();
                        signWithAnotherEmail(email, id);
                    }).start();
                else {
                    mToastUtil.showNaverSignInFail();
                    String code = mOAuthLogin.getLastErrorCode(requireContext()).getCode();
                    String desc = mOAuthLogin.getLastErrorDesc(requireContext());
                    Log.d(TAG, "errorCode: " + code + "\n errorDesc: " + desc);
                    hideIndicator();
                }
            }
        });
    }

    private JsonElement getNaverUserInfo() {
        String url = getString(R.string.naver_profile_api_url);
        String userInfo = mOAuthLogin.requestApi(requireContext(), mOAuthLogin.getAccessToken(requireContext()), url);
        return JsonParser.parseString(userInfo).getAsJsonObject().get("response");
    }

    private void signWithAnotherEmail(String email, String id) {
        mAuth.signInWithEmailAndPassword(email, id).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                showAnotherEmailSignInSuccessToast(email);
                mNavController.popBackStack();
                hideIndicatorInThread();
            } else if (task.getException() instanceof FirebaseNetworkException) {
                mToastUtil.showCheckNetwork();
                hideIndicatorInThread();
            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                createUserAnotherEmail(email, id);
            } else {
                mToastUtil.showUnknownError();
                Log.d(TAG, "signWithAnotherEmail: " + task.getException());
            }
        });
    }

    private void hideIndicatorInThread() {
        mActivityBinding.indicator.post(() -> mActivityBinding.indicator.hide());
    }

    private void showAnotherEmailSignInSuccessToast(String email) {
        if (isNaver(email))
            mToastUtil.showNaverSignInSuccess();
        else if (isKaKao(email))
            mToastUtil.showKakaoSignInSuccess();
    }

    private void showAnotherEmailSignInFailToast(String email) {
        if (isNaver(email))
            mToastUtil.showNaverSignInFail();
        else if (isKaKao(email))
            mToastUtil.showKakaoSignInFail();
    }

    private boolean isKaKao(String email) {
        return email.startsWith("kakao");
    }

    private boolean isNaver(String email) {
        return email.startsWith("naver");
    }

    private void createUserAnotherEmail(String email, String id) {
        mAuth.createUserWithEmailAndPassword(email, id)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        updateUserProfile();
                        showAnotherEmailSignInSuccessToast(email);
                        mNavController.popBackStack();
                    } else {
                        showAnotherEmailSignInFailToast(email);
                        Log.d(TAG, "createUserAnotherEmail: " + task.getException());
                    }
                    hideIndicatorInThread();
                });
    }

    private void updateUserProfile() {
        if (mAuth.getCurrentUser() != null)
            mAuth.getCurrentUser().updateProfile(getUserProfileChangeRequest());
    }

    private UserProfileChangeRequest getUserProfileChangeRequest() {
        return new UserProfileChangeRequest.Builder()
                .setDisplayName(mNickname)
                .setPhotoUri(Uri.parse(mProfileUrl))
                .build();
    }

    public void kakaoSignIn() {
        showIndicator();
        if (mUserApiClient.isKakaoTalkLoginAvailable(requireContext()))
            mUserApiClient.loginWithKakaoTalk(requireContext(), getKakaoCallBack());
        else mUserApiClient.loginWithKakaoAccount(requireContext(), getKakaoCallBack());
    }

    @NotNull
    private Function2<OAuthToken, Throwable, Unit> getKakaoCallBack() {
        return (oAuthToken, throwable) -> {
            if (oAuthToken != null)
                callKakaoUserInfo();
            else if (throwable != null) {
                mToastUtil.showKakaoSignInFail();
                Log.d(TAG, "kakaoSignIn: " + throwable);
            }
            return null;
        };
    }

    private void callKakaoUserInfo() {
        mUserApiClient.me((user, throwable) -> {
            if (user != null)
                signWithKaKaoEmail(user);
            else {
                mToastUtil.showKakaoSignInFailCausePermission();
                Log.d(TAG, "createUserWithKaKaoEmail: " + throwable);
                hideIndicator();
            }
            return null;
        });
    }

    private void signWithKaKaoEmail(User user) {
        Account kakaoAccount = user.getKakaoAccount();
        if (kakaoAccount != null && kakaoAccount.getProfile() != null) {
            String email = "kakao_" + kakaoAccount.getEmail();
            String id = String.valueOf(user.getId());
            mNickname = kakaoAccount.getProfile().getNickname();
            mProfileUrl = kakaoAccount.getProfile().getProfileImageUrl();
            signWithAnotherEmail(email, id);
        } else {
            mToastUtil.showKakaoSignInFailCausePermission();
            hideIndicator();
        }
    }

    public void navigateSignUpFragment() {
        CommonUtil.navigate(mNavController, R.id.signInFragment, SignInFragmentDirections.toSignUpFragment());
    }

    public void navigateResetPasswordFragment() {
        CommonUtil.navigate(mNavController, R.id.signInFragment, SignInFragmentDirections.toResetPasswordFragment());
    }

    public void signInAnonymously() {
        showIndicator();
        mAuth.signInAnonymously()
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        mToastUtil.showAnonymouslySignInSuccess();
                        mNavController.popBackStack();
                    } else {
                        emailSignInFail(task.getException());
                        mToastUtil.showAnonymouslySignInFail();
                    }
                    hideIndicator();
                });
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }
}