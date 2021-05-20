package com.example.myfit.ui.dialog.eidttext.add;

import androidx.lifecycle.SavedStateHandle;

import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public abstract class BaseAddViewModel extends BaseEditTextViewModel {
    @Inject
    public BaseAddViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public abstract void insert();
}
