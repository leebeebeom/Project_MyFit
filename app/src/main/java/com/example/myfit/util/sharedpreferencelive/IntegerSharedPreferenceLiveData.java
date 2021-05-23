package com.example.myfit.util.sharedpreferencelive;

import android.content.SharedPreferences;

public class IntegerSharedPreferenceLiveData extends SharedPreferenceLiveData<Integer> {

    public IntegerSharedPreferenceLiveData(SharedPreferences preference, String key, int defaultValue) {
        super(preference, key, defaultValue);
    }

    @Override
    public Integer getValueFromPreferences(String key, Integer defaultValue) {
        return preference.getInt(key, defaultValue);
    }
}