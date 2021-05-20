package com.example.myfit.ui.dialog.eidttext.edit;

import androidx.lifecycle.SavedStateHandle;

import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public abstract class BaseEditViewModel extends BaseEditTextViewModel {

    @Inject
    public BaseEditViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public abstract void update();
}
