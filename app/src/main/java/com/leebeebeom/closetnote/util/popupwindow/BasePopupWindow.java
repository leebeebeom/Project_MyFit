package com.leebeebeom.closetnote.util.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;

import com.leebeebeom.closetnote.databinding.LayoutPopupBinding;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public abstract class BasePopupWindow extends PopupWindow {

    protected final LayoutPopupBinding mBinding;

    public BasePopupWindow(Context context) {
        mBinding = LayoutPopupBinding.inflate(LayoutInflater.from(context));
        setContentView(mBinding.getRoot());
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
    }

    public static class MainPopupWindow extends BasePopupWindow {

        @Inject
        public MainPopupWindow(@ActivityContext Context context, Fragment fragment) {
            super(context);
            mBinding.tvCreateFolder.setVisibility(View.GONE);
            mBinding.setMainListener((PopupWindowListener.MainPopupWindowListener) fragment);
        }
    }

    public static class ListPopupWindow extends BasePopupWindow {

        @Inject
        public ListPopupWindow(@ActivityContext Context context, Fragment fragment) {
            super(context);
            mBinding.tvAddCategory.setVisibility(View.GONE);
            mBinding.setListListener((PopupWindowListener.ListPopupWindowListener) fragment);
        }
    }
}
