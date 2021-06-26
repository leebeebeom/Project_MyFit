package com.leebeebeom.closetnote.ui.signin.resetpassword;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.util.LiveDataUtil;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class ResetPasswordViewModel extends ViewModel {
    @Getter
    private final MutableLiveData<String> mEmailLive = new MutableLiveData<>("");

    String getEmail() {
        return LiveDataUtil.getStringValue(mEmailLive);
    }
}
