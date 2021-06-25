package com.leebeebeom.closetnote.ui.signin.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.util.LiveDataUtil;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m")
public class SignUpViewModel extends ViewModel {
    private final MutableLiveData<String> mUsernameLive = new MutableLiveData<>("");
    private final MutableLiveData<String> mEmailLive = new MutableLiveData<>("");
    private final MutableLiveData<String> mPasswordLive = new MutableLiveData<>("");
    private final MutableLiveData<String> mConfirmPasswordLive = new MutableLiveData<>("");

    boolean isTextsNotEmpty() {
        return !isUsernameEmpty() && !isEmailEmpty() && !isPasswordEmpty() && !isConfirmPasswordEmpty();
    }

    boolean isPasswordNotEquals() {
        return !LiveDataUtil.getStringValue(mPasswordLive).equals(LiveDataUtil.getStringValue((mConfirmPasswordLive)));
    }

    boolean isUsernameEmpty() {
        return LiveDataUtil.isStringValueEmpty(mUsernameLive);
    }

    boolean isEmailEmpty() {
        return LiveDataUtil.isStringValueEmpty(mEmailLive);
    }

    boolean isPasswordEmpty() {
        return LiveDataUtil.isStringValueEmpty(mPasswordLive);
    }

    boolean isConfirmPasswordEmpty() {
        return LiveDataUtil.isStringValueEmpty(mConfirmPasswordLive);
    }

    String getEmail() {
        return LiveDataUtil.getStringValue(mEmailLive);
    }

    String getPassword() {
        return LiveDataUtil.getStringValue(mPasswordLive);
    }

    String getUserName() {
        return LiveDataUtil.getStringValue(mUsernameLive);
    }
}