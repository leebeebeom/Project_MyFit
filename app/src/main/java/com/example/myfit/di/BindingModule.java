package com.example.myfit.di;

import android.view.LayoutInflater;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.databinding.LayoutDialogSortBinding;
import com.example.myfit.databinding.LayoutPopupBinding;
import com.example.myfit.databinding.TitleActionModeBinding;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;

@Module
@InstallIn(FragmentComponent.class)
public class BindingModule {
    @Provides
    public static ItemDialogEditTextBinding provideDialogBiding(LayoutInflater inflater) {
        return ItemDialogEditTextBinding.inflate(inflater);
    }

    @Provides
    public static LayoutDialogSortBinding provideSortBinding(LayoutInflater inflater) {
        return LayoutDialogSortBinding.inflate(inflater);
    }

    @Provides
    public static TitleActionModeBinding provideActionModeTitleBinding(LayoutInflater inflater){
        return TitleActionModeBinding.inflate(inflater);
    }

    @Provides
    public static LayoutPopupBinding provideLayoutPopupBinding(LayoutInflater inflater){
        return LayoutPopupBinding.inflate(inflater);
    }
}
