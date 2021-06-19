package com.leebeebeom.closetnote.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class SizeLiveSet<E> extends HashSet<E> {
    @Getter
    private final MutableLiveData<Integer> mLiveData = new MutableLiveData<>(0);


    public void add2(E e) {
        super.add(e);
        mLiveData.setValue(size());
    }

    public void addAll2(@NonNull @NotNull Collection<? extends E> c) {
        super.addAll(c);
        mLiveData.setValue(size());
    }


    public void remove2(@Nullable @org.jetbrains.annotations.Nullable Object o) {
        super.remove(o);
        mLiveData.setValue(size());
    }

    public void clear2() {
        super.clear();
        mLiveData.setValue(size());
    }

    public int getLiveDataValue() {
        if (mLiveData.getValue() == null)
            return 0;
        else return mLiveData.getValue();
    }
}
