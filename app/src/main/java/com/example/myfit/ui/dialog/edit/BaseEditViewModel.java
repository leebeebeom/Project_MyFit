package com.example.myfit.ui.dialog.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseEditViewModel extends ViewModel {
    private final MediatorLiveData<Boolean> isExistingMutable = new MediatorLiveData<>();

    public MutableLiveData<Boolean> getIsExistingMutable() {
        isExistingMutable.addSource(getIsExistingLive(), isExistingMutable::setValue);
        return isExistingMutable;
    }

    protected abstract LiveData<Boolean> getIsExistingLive();

    public abstract void queryIsExistingName(long id, String inputText, byte parentIndex);

    public abstract void queryIsExistingName(long id, String inputText, long parentId);

    public abstract void update();
}
