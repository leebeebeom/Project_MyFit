package com.example.myfit.ui.dialog.eidttext;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public abstract class BaseEditTextViewModel extends ViewModel {
    @Getter
    @Setter
    protected String mName;

    public abstract MutableLiveData<Boolean> getExistingNameLive();

    public abstract static class BaseAddViewModel extends BaseEditTextViewModel {
        public abstract void insert();
    }

    public abstract static class BaseEditViewModel extends BaseEditTextViewModel {
        public abstract void update();
    }
}
