package com.example.myfit.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class SizeLiveSet<E> extends LinkedHashSet<E> {
    @Getter
    private final MutableLiveData<Integer> mLiveData = new MutableLiveData<>(0);

    @Override
    public boolean add(E e) {
        mLiveData.setValue(size() + 1);
        return super.add(e);
    }

    @Override
    public boolean addAll(@NonNull @NotNull Collection<? extends E> c) {
        mLiveData.setValue(size() + c.size());
        return super.addAll(c);
    }

    @Override
    public boolean remove(@Nullable @org.jetbrains.annotations.Nullable Object o) {
        mLiveData.setValue(size() - 1);
        return super.remove(o);
    }

    @Override
    public boolean removeAll(@NonNull @NotNull Collection<?> c) {
        mLiveData.setValue(0);
        return super.removeAll(c);
    }

    @Override
    public void clear() {
        mLiveData.setValue(0);
        super.clear();
    }

    public int getLiveDataValue() {
        if (mLiveData.getValue() == null)
            return 0;
        else return mLiveData.getValue();
    }
}
