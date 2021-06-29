package com.leebeebeom.closetnote.util.auth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;

import com.leebeebeom.closetnote.data.model.model.UserInfo;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.util.ToastUtil;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class BaseAuth {
    protected final ActivityMainBinding mActivityBinding;
    protected final Context mContext;
    protected final ToastUtil mToastUtil;
    private boolean isConnect;

    @Inject
    public BaseAuth(@ActivityContext Context context, ActivityMainBinding activityBinding, ToastUtil toastUtil) {
        mContext = context;
        mActivityBinding = activityBinding;
        mToastUtil = toastUtil;
        registerNetworkCallback();
    }

    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isConnect = true;
            }

            @Override
            public void onLost(@NonNull Network network) {
                mToastUtil.showCheckNetwork();
                isConnect = false;
            }
        });
    }

    protected void showIndicator() {
        mActivityBinding.indicator.show();
    }

    protected void hideIndicator() {
        mActivityBinding.indicator.hide();
    }

    protected void hideIndicatorInThread() {
        mActivityBinding.indicator.post(mActivityBinding.indicator::hide);
    }

    protected boolean isConnect() {
        if (!isConnect)
            mToastUtil.showCheckNetwork();
        return isConnect;
    }

    protected void showAnotherEmailSignInSuccessToast(UserInfo userInfo) {
        if (isNaver(userInfo))
            mToastUtil.showNaverSignInSuccess();
        else if (isKaKao(userInfo))
            mToastUtil.showKakaoSignInSuccess();
    }

    protected void showAnotherEmailSignInFailToast(UserInfo userInfo) {
        if (isNaver(userInfo))
            mToastUtil.showNaverSignInFail();
        else if (isKaKao(userInfo))
            mToastUtil.showKakaoSignInFail();
    }

    protected boolean isKaKao(UserInfo userInfo) {
        return userInfo.getEmail().startsWith("kakao");
    }

    protected boolean isNaver(UserInfo userInfo) {
        return userInfo.getEmail().startsWith("naver");
    }
}
