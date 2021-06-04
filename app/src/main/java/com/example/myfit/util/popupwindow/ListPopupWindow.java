package com.example.myfit.util.popupwindow;

import android.view.View;

import com.example.myfit.databinding.LayoutPopupBinding;

import javax.inject.Inject;

public class ListPopupWindow extends BasePopupWindow<PopupWindowListener.ListPopupWindowListener> {

    @Inject
    public ListPopupWindow(LayoutPopupBinding binding) {
        super(binding);
        binding.tvAddCategory.setVisibility(View.GONE);
    }

    @Override
    public void setListener(PopupWindowListener.ListPopupWindowListener listener) {
        mBinding.setListListener(listener);
    }
}
