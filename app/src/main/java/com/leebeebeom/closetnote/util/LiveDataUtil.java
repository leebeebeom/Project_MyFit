package com.leebeebeom.closetnote.util;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

public class LiveDataUtil {
    public static boolean isStringValueEmpty(LiveData<String> stringLiveData) {
        if (stringLiveData.getValue() != null)
            return TextUtils.isEmpty(stringLiveData.getValue().trim());
        else return true;
    }

    public static String getStringValue(LiveData<String> stringLiveData) {
        if (stringLiveData.getValue() != null)
            return stringLiveData.getValue();
        else return "";
    }
}
