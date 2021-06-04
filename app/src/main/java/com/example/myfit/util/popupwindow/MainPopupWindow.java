package com.example.myfit.util.popupwindow;

import android.view.View;

import com.example.myfit.databinding.LayoutPopupBinding;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class MainPopupWindow extends BasePopupWindow<PopupWindowListener.MainPopupWindowListener> {

    @Inject
    public MainPopupWindow(LayoutPopupBinding biding) {
        super(biding);
        biding.tvCreateFolder.setVisibility(View.GONE);
    }

    @Override
    public void setListener(PopupWindowListener.MainPopupWindowListener listener) {
        mBinding.setMainListener(listener);
    }
}
