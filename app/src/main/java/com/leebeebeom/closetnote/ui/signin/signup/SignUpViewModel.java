package com.leebeebeom.closetnote.ui.signin.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.data.model.model.UserInfo;
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

    public boolean isTextsNotEmpty() {
        return !isUsernameEmpty() && !isEmailEmpty() && !isPasswordEmpty() && !isConfirmPasswordEmpty();
    }

    public boolean isPasswordEquals() {
        return LiveDataUtil.getStringValue(mPasswordLive).equals(LiveDataUtil.getStringValue((mConfirmPasswordLive)));
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

    UserInfo getUserInfo() {
        return UserInfo.builder()
                .email(LiveDataUtil.getStringValue(mEmailLive))
                .password(LiveDataUtil.getStringValue(mPasswordLive))
                .nickname(LiveDataUtil.getStringValue(mUsernameLive))
                .build();
    }
}