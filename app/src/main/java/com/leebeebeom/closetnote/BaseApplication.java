package com.leebeebeom.closetnote;

import androidx.multidex.MultiDexApplication;

import com.kakao.sdk.common.KakaoSdk;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BaseApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, getString(R.string.kakao_native_key));
    }
}
