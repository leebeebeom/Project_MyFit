package com.leebeebeom.closetnote.ui.signin;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
@Getter
public class SignInViewModel extends ViewModel {
    private final MutableLiveData<String> mEmailLive = new MutableLiveData<>("");
    private final MutableLiveData<String> mPasswordLive = new MutableLiveData<>("");

    boolean isTextsNotEmpty() {
        return !isEmailEmpty() && !isPasswordEmpty();
    }

    boolean isEmailEmpty() {
        return TextUtils.isEmpty(mEmailLive.getValue());
    }

    boolean isPasswordEmpty() {
        return TextUtils.isEmpty(mPasswordLive.getValue());
    }

    String getEmail() {
        return String.valueOf(mEmailLive.getValue());
    }

    String getPassword() {
        return String.valueOf(mPasswordLive.getValue());
    }
}