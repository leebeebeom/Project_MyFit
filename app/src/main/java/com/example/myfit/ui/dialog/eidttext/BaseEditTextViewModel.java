package com.example.myfit.ui.dialog.eidttext;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public abstract class BaseEditTextViewModel extends ViewModel {
    private static final String QUERY = "query";
    private final SavedStateHandle savedStateHandle;
    private final MediatorLiveData<Boolean> isExistingMutable = new MediatorLiveData<>();

    @Inject
    public BaseEditTextViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    public MutableLiveData<Boolean> getIsExistingMutable() {
        isExistingMutable.addSource(getIsExistingLive(), isExistingMutable::setValue);
        return isExistingMutable;
    }

    private LiveData<Boolean> getIsExistingLive() {
        MutableLiveData<String> stateHandleLiveData = savedStateHandle.getLiveData(QUERY);
        return Transformations.switchMap(stateHandleLiveData, this::getIsExistingLive);
    }

    protected abstract LiveData<Boolean> getIsExistingLive(String name);

    protected void setStateHandle(String name) {
        savedStateHandle.set(QUERY, name);
    }
}
