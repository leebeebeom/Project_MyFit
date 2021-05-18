package com.example.myfit.ui.dialog.edit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseEditViewModel extends ViewModel {
    public abstract MutableLiveData<Boolean> getIsExistingLive();

    public abstract void queryIsExistingName(long id, String inputText, byte parentIndex);

    public abstract void queryIsExistingName(long id, String inputText, long parentId);

    public abstract void update();
}
