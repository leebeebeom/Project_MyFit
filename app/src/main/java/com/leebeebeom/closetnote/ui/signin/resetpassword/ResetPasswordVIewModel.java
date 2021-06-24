package com.leebeebeom.closetnote.ui.signin.resetpassword;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class ResetPasswordVIewModel extends ViewModel {
    @Getter
    private final MutableLiveData<String> mEmailLive = new MutableLiveData<>("");

    String getEmail() {
        return String.valueOf(mEmailLive.getValue());
    }

    boolean isEmailEmpty() {
        return TextUtils.isEmpty(mEmailLive.getValue());
    }
}
