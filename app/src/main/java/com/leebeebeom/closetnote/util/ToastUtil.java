package com.leebeebeom.closetnote.util;

import android.content.Context;
import android.widget.Toast;

import com.leebeebeom.closetnote.R;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class ToastUtil {
    private final Context mContext;

    @Inject
    public ToastUtil(@ApplicationContext Context context) {
        mContext = context;
    }

    public void showUnknownError() {
        Toast.makeText(mContext, R.string.toast_fail_unknown_error, Toast.LENGTH_SHORT).show();
    }

    public void showEmailReSent() {
        Toast.makeText(mContext, R.string.toast_re_send_verification_mail, Toast.LENGTH_SHORT).show();
    }

    public void showCheckNetwork() {
        Toast.makeText(mContext, R.string.toast_check_network, Toast.LENGTH_SHORT).show();
    }

    public void showGoogleSignInFail() {
        Toast.makeText(mContext, R.string.toast_google_sign_in_fail, Toast.LENGTH_SHORT).show();
    }

    public void showGoogleSignInSuccess() {
        Toast.makeText(mContext, R.string.toast_google_sign_in_success, Toast.LENGTH_SHORT).show();
    }

    public void showNaverSignInFail() {
        Toast.makeText(mContext, R.string.toast_naver_sign_in_fail, Toast.LENGTH_SHORT).show();
    }

    public void showNaverSignInSuccess() {
        Toast.makeText(mContext, R.string.toast_naver_sign_in_success, Toast.LENGTH_SHORT).show();
    }

    public void showKakaoSignInSuccess() {
        Toast.makeText(mContext, R.string.toast_kakao_sign_in_success, Toast.LENGTH_SHORT).show();
    }

    public void showKakaoSignInFail() {
        Toast.makeText(mContext, R.string.toast_kakao_sign_in_fail, Toast.LENGTH_SHORT).show();
    }

    public void showKakaoSignInFailCausePermission() {
        Toast.makeText(mContext, R.string.toast_kakao_sing_in_fail_permissioon, Toast.LENGTH_SHORT).show();
    }

    public void showEmailSignInSuccess() {
        Toast.makeText(mContext, R.string.toast_sign_in_success, Toast.LENGTH_SHORT).show();
    }

    public void showAnonymouslySignInSuccess() {
        Toast.makeText(mContext, R.string.toast_sign_in_anonymously_success, Toast.LENGTH_SHORT).show();
    }

    public void showAnonymouslySignInFail() {
        Toast.makeText(mContext, R.string.toast_sign_in_anonymously_success, Toast.LENGTH_SHORT).show();
    }
}
