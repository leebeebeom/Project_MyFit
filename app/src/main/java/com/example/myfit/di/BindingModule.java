package com.example.myfit.di;

import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.example.myfit.R;
import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.databinding.LayoutDialogSortBinding;
import com.example.myfit.databinding.LayoutDialogTreeBinding;
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
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(inflater);
        binding.et.requestFocus();
        showErrorIfMoreThan30Characters(binding);
        return binding;
    }

    private static void showErrorIfMoreThan30Characters(ItemDialogEditTextBinding binding) {
        binding.et.setOnKeyListener((v, keyCode, event) -> {
            if (isTextLength30(binding) && keyCode != KeyEvent.KEYCODE_DEL && keyCode != KeyEvent.FLAG_EDITOR_ACTION)
                binding.layout.setError(v.getContext().getString(R.string.dialog_et_max_length));
            else binding.layout.setErrorEnabled(false);
            return false;
        });
    }

    private static boolean isTextLength30(ItemDialogEditTextBinding binding) {
        return String.valueOf(binding.et.getText()).length() == 30;
    }

    @Provides
    public static LayoutDialogSortBinding provideSortBinding(LayoutInflater inflater) {
        return LayoutDialogSortBinding.inflate(inflater);
    }

    @Provides
    public static TitleActionModeBinding provideActionModeTitleBinding(LayoutInflater inflater) {
        return TitleActionModeBinding.inflate(inflater);
    }

    @Provides
    public static LayoutPopupBinding provideLayoutPopupBinding(LayoutInflater inflater){
        return LayoutPopupBinding.inflate(inflater);
    }

    @Provides
    public static LayoutDialogTreeBinding provideTreeViewBinding(LayoutInflater inflater){
        return LayoutDialogTreeBinding.inflate(inflater);
    }
}
