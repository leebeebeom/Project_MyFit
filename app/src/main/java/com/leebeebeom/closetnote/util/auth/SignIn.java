package com.leebeebeom.closetnote.util.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.Profile;
import com.kakao.sdk.user.model.User;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.data.model.model.UserInfo;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.util.ToastUtil;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static com.leebeebeom.closetnote.ui.MainActivity.TAG;

public class SignIn extends BaseAuth {

    private final FirebaseAuth mAuth;
    private final NavController mNavController;
    private final OAuthLogin mOAuthLogin;
    private final SignUp mSignUp;
    private final UserApiClient mUserApiClient;

    @Inject
    public SignIn(@ActivityContext Context context, ActivityMainBinding activityBinding, ToastUtil toastUtil,
                  FirebaseAuth auth, OAuthLogin oAuthLogin, UserApiClient userApiClient, NavController navController, SignUp signUp) {
        super(context, activityBinding, toastUtil);
        mAuth = auth;
        mNavController = navController;
        mOAuthLogin = oAuthLogin;
        mSignUp = signUp;
        mUserApiClient = userApiClient;
    }

    public void emailSignIn(UserInfo userInfo, EmailSignInListener emailSignInListener) {
        if (isConnect()) {
            showIndicator();
            mAuth.signInWithEmailAndPassword(userInfo.getEmail(), userInfo.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) emailSignInSuccess(emailSignInListener);
                        else emailSignInListener.emailSignInFail(task.getException());
                        hideIndicator();
                    });
        }
    }

    private void emailSignInSuccess(EmailSignInListener emailSignInListener) {
        if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isEmailVerified())
            emailSignInListener.emailSignInNotVerification();
        else {
            mToastUtil.showEmailSignInSuccess();
            mNavController.popBackStack();
        }
    }

    public void signInWithGoogle(GoogleSignInAccount account) {
        if (isConnect() && account != null) {
            showIndicator();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mToastUtil.showGoogleSignInSuccess();
                            mNavController.popBackStack();
                        } else {
                            mToastUtil.showGoogleSignInFail();
                            Log.d(TAG, "SignInFragment : signInWithGoogle : !task.isSuccessful" + task.getException());
                        }
                        hideIndicator();
                    });
        }
    }

    @SuppressLint("HandlerLeak")
    public void naverSignIn() {
        if (isConnect()) {
            showIndicator();
            mOAuthLogin.startOauthLoginActivity((AppCompatActivity) mContext, new OAuthLoginHandler() {
                @Override
                public void run(boolean success) {
                    if (success)
                        new Thread(() -> {
                            UserInfo userInfo = getNaverUserInfo();
                            signWithAnotherEmail(userInfo);
                        }).start();
                    else {
                        mToastUtil.showNaverSignInFail();
                        String code = mOAuthLogin.getLastErrorCode(mContext).getCode();
                        String desc = mOAuthLogin.getLastErrorDesc(mContext);
                        Log.d(TAG, "SignFragment : naverSignIn : errorCode: " + code + "\n errorDesc: " + desc);
                        hideIndicator();
                    }
                }
            });
        }
    }

    private UserInfo getNaverUserInfo() {
        String url = mContext.getString(R.string.naver_profile_api_url);
        String userInfo = mOAuthLogin.requestApi(mContext, mOAuthLogin.getAccessToken(mContext), url);
        JsonElement response = JsonParser.parseString(userInfo).getAsJsonObject().get("response");

        String email = "naver_" + response.getAsJsonObject().get("email").getAsString();
        //TODO 암호 만들어야함
        String id = response.getAsJsonObject().get("id").getAsString();
        String nickname = response.getAsJsonObject().get("nickname").getAsString();
        String profileUrl = response.getAsJsonObject().get("profile_image").getAsString();
        return UserInfo.builder()
                .email(email)
                .password(id)
                .nickname(nickname)
                .profileUrl(profileUrl)
                .build();
    }

    private void signWithAnotherEmail(UserInfo userInfo) {
        mAuth.signInWithEmailAndPassword(userInfo.getEmail(), userInfo.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showAnotherEmailSignInSuccessToast(userInfo);
                mNavController.popBackStack();
                hideIndicatorInThread();
            } else if (task.getException() instanceof FirebaseAuthInvalidUserException)
                mSignUp.createUserAnotherEmail(userInfo);
            else {
                mToastUtil.showUnknownError();
                Log.d(TAG, "SignInFragment : signWithAnotherEmail: " + task.getException());
            }
        });
    }

    public void kakaoSignIn() {
        if (isConnect()) {
            showIndicator();
            if (mUserApiClient.isKakaoTalkLoginAvailable(mContext))
                mUserApiClient.loginWithKakaoTalk(mContext, getKakaoCallBack());
            else mUserApiClient.loginWithKakaoAccount(mContext, getKakaoCallBack());
        }
    }

    @NotNull
    private Function2<OAuthToken, Throwable, Unit> getKakaoCallBack() {
        return (oAuthToken, throwable) -> {
            if (oAuthToken != null)
                callKakaoUserInfo();
            else if (throwable != null) {
                mToastUtil.showKakaoSignInFail();
                Log.d(TAG, "SignInFragment : kakaoSignIn: " + throwable);
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
                Log.d(TAG, "SignInFragment : createUserWithKaKaoEmail: " + throwable);
                hideIndicator();
            }
            return null;
        });
    }

    private void signWithKaKaoEmail(User user) {
        Account kakaoAccount = user.getKakaoAccount();
        if (kakaoAccount != null && kakaoAccount.getProfile() != null) {
            UserInfo userInfo = getKakaoUserInfo(user, kakaoAccount, kakaoAccount.getProfile());
            signWithAnotherEmail(userInfo);
        } else {
            mToastUtil.showKakaoSignInFailCausePermission();
            hideIndicator();
        }
    }

    private UserInfo getKakaoUserInfo(@NotNull User user, @NotNull Account kakaoAccount, @NotNull Profile profile) {
        String email = "kakao_" + kakaoAccount.getEmail();
        //TODO 암호 만들어야함
        String id = String.valueOf(user.getId());
        String nickname = profile.getNickname();
        String profileUrl = profile.getProfileImageUrl();
        return UserInfo.builder()
                .email(email)
                .password(id)
                .nickname(nickname)
                .profileUrl(profileUrl)
                .build();
    }

    public void signInAnonymously() {
        if (isConnect()) {
            showIndicator();
            mAuth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mToastUtil.showAnonymouslySignInSuccess();
                            mNavController.popBackStack();
                        } else {
                            mToastUtil.showAnonymouslySignInFail();
                            Log.d(TAG, "SignInFragment : signInAnonymously: " + task.getException());
                        }
                        hideIndicator();
                    });
        }
    }

    public interface EmailSignInListener {
        void emailSignInNotVerification();

        void emailSignInFail(Exception e);
    }
}
