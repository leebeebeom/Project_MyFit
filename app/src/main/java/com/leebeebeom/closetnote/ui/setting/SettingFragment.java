package com.leebeebeom.closetnote.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leebeebeom.closetnote.databinding.FragmentSettingBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;

import org.jetbrains.annotations.NotNull;

public class SettingFragment extends BaseFragment {

    private FragmentSettingBinding mMBinding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mMBinding = FragmentSettingBinding.inflate(inflater, container, false);
        return mMBinding.getRoot();
    }

    @Override
    public LockableScrollView getScrollView() {
        return mMBinding.sv;
    }
}
