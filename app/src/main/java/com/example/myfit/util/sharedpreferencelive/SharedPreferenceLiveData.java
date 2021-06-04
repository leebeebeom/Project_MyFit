package com.example.myfit.util.sharedpreferencelive;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

public abstract class SharedPreferenceLiveData<T> extends LiveData<T> {

    protected final SharedPreferences mPreference;
    private final String mKey;
    private final T mDefaultValue;
    private final SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener;

    public SharedPreferenceLiveData(SharedPreferences preference, String key, T defaultValue) {
        this.mPreference = preference;
        this.mKey = key;
        this.mDefaultValue = defaultValue;

        mPreferenceChangeListener = (sharedPreferences, key1) -> {
            if (SharedPreferenceLiveData.this.mKey.equals(key1))
                setValue(getValueFromPreferences(key1, defaultValue));
        };
    }

    protected abstract T getValueFromPreferences(String key, T defValue);

    @Override
    protected void onActive() {
        super.onActive();
        setValue(getValueFromPreferences(mKey, mDefaultValue));
        mPreference.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        mPreference.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        super.onInactive();
    }


}
