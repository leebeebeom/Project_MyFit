package com.example.myfit.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavBackStackEntry;

public class MainGraphViewModel extends ViewModel {
    private MutableLiveData<NavBackStackEntry> backStackEntryLive;

    public void setBackStackEntryLive(NavBackStackEntry backStackEntry) {
        backStackEntryLive.setValue(backStackEntry);
    }

    public MutableLiveData<NavBackStackEntry> getBackStackEntryLive() {
        if (backStackEntryLive == null) backStackEntryLive = new MutableLiveData<>();
        return backStackEntryLive;
    }
}
