package com.example.myfit.util.popupwindow;

import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.myfit.databinding.LayoutPopupBinding;

public abstract class BasePopupWindow<T extends PopupWindowListener> extends PopupWindow {

    protected final LayoutPopupBinding mBinding;

    public BasePopupWindow(LayoutPopupBinding binding) {
        super(binding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBinding = binding;
        setOutsideTouchable(true);
    }

    public abstract void setListener(T t);
}
