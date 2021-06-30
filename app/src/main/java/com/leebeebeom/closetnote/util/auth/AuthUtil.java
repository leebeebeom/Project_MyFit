package com.leebeebeom.closetnote.util.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.auth.AuthApiClient;
import com.leebeebeom.closetnote.data.model.model.UserInfo;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.data.OAuthLoginState;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class AuthUtil {
    private final Context mContext;
    private final FirebaseAuth mAuth;
    private final OAuthLogin mOAuthLogin;
    private final SignIn mSignIn;
    private final Verification mVerification;
    private final SignUp mSignUp;

    @Inject
    public AuthUtil(@ActivityContext Context context, FirebaseAuth auth, OAuthLogin oAuthLogin,
                    SignIn signIn, SignUp signUp, Verification verification) {
        mContext = context;
        mAuth = auth;
        mOAuthLogin = oAuthLogin;
        mSignIn = signIn;
        mVerification = verification;
        mSignUp = signUp;
    }

    public void emailSignIn(UserInfo userInfo, SignIn.EmailSignInListener listener) {
        mSignIn.emailSignIn(userInfo, listener);
    }

    public void signInWithGoogle(GoogleSignInAccount account) {
        mSignIn.signInWithGoogle(account);
    }

    public void naverSignIn() {
        mSignIn.naverSignIn();
    }

    public void kakaoSignIn() {
        mSignIn.kakaoSignIn();
    }

    public void signInAnonymously() {
        mSignIn.signInAnonymously();
    }

    public void sendPasswordResetEmail(String email, Verification.PasswordResetEmailListener listener) {
        mVerification.sendPasswordResetEmail(email, listener);
    }

    public void reSendVerificationEmail() {
        mVerification.reSendVerificationEmail();
    }

    public boolean isNotSignIn() {
        if (mAuth.getCurrentUser() == null)
            return true;
        else {
            if (mAuth.getCurrentUser().getEmail() == null || TextUtils.isEmpty(mAuth.getCurrentUser().getEmail()))
                return false;
            else return !mAuth.getCurrentUser().isEmailVerified() &&
                    mOAuthLogin.getState(mContext) == OAuthLoginState.NEED_LOGIN && !AuthApiClient.getInstance().hasToken();
        }
    }

    public void openEmail(String email) {
        String domain = getDomain(email);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(domain));
        if (intent.resolveActivity(mContext.getPackageManager()) != null)
            mContext.startActivity(intent);
    }

    private String getDomain(String email) {
        int index = email.indexOf("@");
        return "https://" + email.substring(index + 1);
    }

    public String getEmail() {
        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getEmail();
        else return "";
    }

    public void emailSignUp(UserInfo userInfo, SignUp.EmailSignUpListener listener) {
        mSignUp.emailSignUp(userInfo, listener);
    }

    public void anonymouslyLinkWithEmail(UserInfo userInfo, SignUp.EmailSignUpListener listener) {
        mSignUp.anonymouslyLinkWithEmail(userInfo, listener);
    }
}
