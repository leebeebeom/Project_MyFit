package com.example.myfit.util.sharedpreferencelive;

import android.content.SharedPreferences;

public class BooleanSharePreferenceLiveData extends SharedPreferenceLiveData<Boolean> {

    public BooleanSharePreferenceLiveData(SharedPreferences preference, String key, Boolean defaultValue) {
        super(preference, key, defaultValue);
    }

    @Override
    protected Boolean getValueFromPreferences(String key, Boolean defValue) {
        return mPreference.getBoolean(key, defValue);
    }
}
