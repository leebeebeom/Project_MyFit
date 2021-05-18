package com.example.myfit.ui.dialog.add;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseAddViewModel extends ViewModel {
    public abstract void insert();

    public abstract MutableLiveData<Boolean> getIsExistingLive();

    public abstract void queryIsExistingName(String inputText, byte parentIndex);

    public abstract void queryIsExistingName(String inputText, long parentId, byte parentIndex);
}
