package com.leebeebeom.closetnote.util.sharedpreferencelive;

import android.content.SharedPreferences;

public class BooleanSharedPreferenceLiveData extends SharedPreferenceLiveData<Boolean> {

    public BooleanSharedPreferenceLiveData(SharedPreferences preference, String key, Boolean defaultValue) {
        super(preference, key, defaultValue);
    }

    @Override
    protected Boolean getValueFromPreferences(String key, Boolean defValue) {
        return mPreference.getBoolean(key, defValue);
    }
}
