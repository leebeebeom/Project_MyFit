package com.example.myfit.ui.dialog.eidttext;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseEditTextViewModel extends ViewModel {
    public abstract MutableLiveData<Boolean> getIsExistingMutable();

    public abstract static class BaseAddViewModel extends BaseEditTextViewModel {
        public abstract void insert();
    }

    public abstract static class BaseEditViewModel extends BaseEditTextViewModel {
        public abstract void update();
    }
}
