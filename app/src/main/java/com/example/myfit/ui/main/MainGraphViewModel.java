package com.example.myfit.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavBackStackEntry;

public class MainGraphViewModel extends ViewModel {
    private MutableLiveData<NavBackStackEntry> mBackStackEntryLive;

    public void setBackStackEntryLive(NavBackStackEntry backStackEntry) {
        mBackStackEntryLive.setValue(backStackEntry);
    }

    public MutableLiveData<NavBackStackEntry> getBackStackEntryLive() {
        if (mBackStackEntryLive == null) mBackStackEntryLive = new MutableLiveData<>();
        return mBackStackEntryLive;
    }
}
