package com.example.myfit.ui.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseDialogViewModel extends ViewModel {
    private final MediatorLiveData<Boolean> isExistingMutable = new MediatorLiveData<>();

    public MutableLiveData<Boolean> getIsExistingMutable() {
        isExistingMutable.addSource(getIsExistingLive(), isExistingMutable::setValue);
        return isExistingMutable;
    }

    public abstract LiveData<Boolean> getIsExistingLive();

    public abstract void queryIsExistingName(String inputText, byte parentIndex);

    public abstract void queryIsExistingName(String inputText, long parentId, byte parentIndex);

    public abstract void queryIsExistingName(long id, String inputText, byte parentIndex);

    public abstract void queryIsExistingName(long id, String inputText, long parentId);

    public abstract void insert();

    public abstract void update();
}
