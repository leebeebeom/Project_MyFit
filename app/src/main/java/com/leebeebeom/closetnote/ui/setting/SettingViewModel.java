package com.leebeebeom.closetnote.ui.setting;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.leebeebeom.closetnote.R;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m")
@HiltViewModel
public class SettingViewModel extends ViewModel {
    private final MutableLiveData<String> mEmailLive = new MutableLiveData<>("");
    private final MutableLiveData<Uri> mProfileUrlLive = new MutableLiveData<>();
    private final MutableLiveData<String> mNicknameLive = new MutableLiveData<>("");
    private final Application mApplication;

    @Inject
    public SettingViewModel(Application application) {
        mApplication = application;
    }

    public void livedataInit(FirebaseUser user) {
        if (user.getEmail() == null || TextUtils.isEmpty(user.getEmail())) {
            mNicknameLive.setValue(mApplication.getString(R.string.setting_anonymously));
            mEmailLive.setValue(mApplication.getString(R.string.setting_anonymously));
        } else {
            mNicknameLive.setValue(user.getDisplayName());
            mEmailLive.setValue(getWithoutPrefixEmail(user.getEmail()));
            mProfileUrlLive.setValue(user.getPhotoUrl());
        }
    }

    private String getWithoutPrefixEmail(String email) {
        if (email.startsWith("naver_"))
            return email.replace("naver_", "");
        else return email.replace("kakao_", "");
    }
}
