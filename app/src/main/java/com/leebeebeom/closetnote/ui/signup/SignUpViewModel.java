package com.leebeebeom.closetnote.ui.signup;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
        return !String.valueOf(mPasswordLive.getValue()).equals(mConfirmPasswordLive.getValue());
    }

    boolean isUsernameEmpty() {
        return TextUtils.isEmpty(mUsernameLive.getValue());
    }

    boolean isEmailEmpty() {
        return TextUtils.isEmpty(mEmailLive.getValue());
    }

    boolean isPasswordEmpty() {
        return TextUtils.isEmpty(mPasswordLive.getValue());
    }

    boolean isConfirmPasswordEmpty() {
        return TextUtils.isEmpty(mConfirmPasswordLive.getValue());
    }

    String getEmail() {
        return String.valueOf(mEmailLive.getValue());
    }

    String getPassword() {
        return String.valueOf(mPasswordLive.getValue());
    }
}