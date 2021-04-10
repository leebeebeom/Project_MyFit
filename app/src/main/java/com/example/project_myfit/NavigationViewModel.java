package com.example.project_myfit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavBackStackEntry;

public class NavigationViewModel extends ViewModel {
    private MutableLiveData<NavBackStackEntry> backStackEntryLive;

    public MutableLiveData<NavBackStackEntry> getBackStackEntryLive() {
        if (backStackEntryLive == null)
            return backStackEntryLive = new MutableLiveData<>();
        else return backStackEntryLive;
    }

    public void backStackEntryLiveSetValue(NavBackStackEntry backStackEntry) {
        backStackEntryLive.setValue(backStackEntry);
    }
}
