package com.example.myfit.util.sharedpreferencelive;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

public abstract class SharedPreferenceLiveData<T> extends LiveData<T> {

    protected final SharedPreferences preference;
    private final String key;
    private final T defaultValue;
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    public SharedPreferenceLiveData(SharedPreferences preference, String key, T defaultValue) {
        this.preference = preference;
        this.key = key;
        this.defaultValue = defaultValue;

        preferenceChangeListener = (sharedPreferences, key1) -> {
            if (SharedPreferenceLiveData.this.key.equals(key1))
                setValue(getValueFromPreferences(key1, defaultValue));
        };
    }

    protected abstract T getValueFromPreferences(String key, T defValue);

    @Override
    protected void onActive() {
        super.onActive();
        setValue(getValueFromPreferences(key, defaultValue));
        preference.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        preference.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onInactive();
    }


}
