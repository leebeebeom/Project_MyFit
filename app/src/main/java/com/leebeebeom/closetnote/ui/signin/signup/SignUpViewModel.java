package com.leebeebeom.closetnote.ui.signin.signup;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
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
        return !getStringValue(mPasswordLive).equals(getStringValue(mConfirmPasswordLive));
    }

    boolean isUsernameEmpty() {
        return isEmpty(mUsernameLive);
    }

    boolean isEmailEmpty() {
        return isEmpty(mEmailLive);
    }

    boolean isPasswordEmpty() {
        return isEmpty(mPasswordLive);
    }

    boolean isConfirmPasswordEmpty() {
        return isEmpty(mConfirmPasswordLive);
    }

    private boolean isEmpty(LiveData<String> stringLiveData) {
        if (stringLiveData.getValue() != null)
            return TextUtils.isEmpty(stringLiveData.getValue().trim());
        else return true;
    }

    String getEmail() {
        return getStringValue(mEmailLive);
    }

    String getPassword() {
        return getStringValue(mPasswordLive);
    }

    String getUserName() {
        return getStringValue(mUsernameLive);
    }

    private String getStringValue(LiveData<String> stringLiveData) {
        if (stringLiveData.getValue() != null)
            return stringLiveData.getValue().trim();
        else return "";
    }
}