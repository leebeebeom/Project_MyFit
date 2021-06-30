package com.leebeebeom.closetnote.ui.signin;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.data.model.model.UserInfo;
import com.leebeebeom.closetnote.util.LiveDataUtil;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
@Getter
public class SignInViewModel extends ViewModel {
    private final MutableLiveData<String> mEmailLive = new MutableLiveData<>("");
    private final MutableLiveData<String> mPasswordLive = new MutableLiveData<>("");

    public boolean isTextsNotEmpty() {
        return !isEmailEmpty() && !isPasswordEmpty();
    }

    boolean isEmailEmpty() {
        return LiveDataUtil.isStringValueEmpty(mEmailLive);
    }

    boolean isPasswordEmpty() {
        return LiveDataUtil.isStringValueEmpty(mPasswordLive);
    }

    private String getEmail() {
        return LiveDataUtil.getStringValue(mEmailLive);
    }

    private String getPassword() {
        return LiveDataUtil.getStringValue(mPasswordLive);
    }

    UserInfo createUserInfo() {
        return UserInfo.builder()
                .email(getEmail())
                .password(getPassword())
                .build();
    }
}