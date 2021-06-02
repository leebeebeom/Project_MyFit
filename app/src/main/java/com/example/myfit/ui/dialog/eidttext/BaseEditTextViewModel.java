package com.example.myfit.ui.dialog.eidttext;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEditTextViewModel extends ViewModel {
    public abstract MutableLiveData<Boolean> getExistingNameLive();

    public abstract static class BaseAddViewModel extends BaseEditTextViewModel {
        public abstract void insert();
    }

    public abstract static class BaseEditViewModel extends BaseEditTextViewModel {
        public abstract void update();
    }
}
