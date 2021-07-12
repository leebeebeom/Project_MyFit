package com.leebeebeom.closetnote;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, getString(R.string.kakao_native_key));
    }
}
