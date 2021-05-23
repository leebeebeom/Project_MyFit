package com.example.myfit.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.myfit.databinding.LayoutPopupBinding;

public class PopupWindowImpl extends PopupWindow {

    public static PopupWindowImpl getCategoryPopupWindow(LayoutInflater inflater, CategoryPopupWindowClickListener listener) {
        LayoutPopupBinding binding = LayoutPopupBinding.inflate(inflater);
        binding.tvCreateFolder.setVisibility(View.GONE);
        return new PopupWindowImpl(binding, listener);
    }

    public static PopupWindowImpl getFolderPopupWindow(LayoutInflater inflater, FolderPopupWindowClickListener listener) {
        LayoutPopupBinding binding = LayoutPopupBinding.inflate(inflater);
        binding.tvCreateFolder.setVisibility(View.GONE);
        return new PopupWindowImpl(binding, listener);
    }

    private PopupWindowImpl(LayoutPopupBinding binding, CategoryPopupWindowClickListener listener) {
        super(binding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setCategoryClickListener(binding, listener);
    }

    private void setCategoryClickListener(LayoutPopupBinding binding, CategoryPopupWindowClickListener listener) {
        binding.tvAddCategory.setOnClickListener(v -> {
            listener.addCategoryClick();
            dismiss();
        });

        binding.tvSortOrder.setOnClickListener(v -> {
            listener.sortClick();
            dismiss();
        });

        binding.tvRecycleBin.setOnClickListener(v -> {
            listener.recycleBinClick();
            dismiss();
        });
    }

    private PopupWindowImpl(LayoutPopupBinding binding, FolderPopupWindowClickListener listener) {
        super(binding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFolderCLickListener(binding, listener);
    }

    private void setFolderCLickListener(LayoutPopupBinding binding, FolderPopupWindowClickListener listener) {
        binding.tvCreateFolder.setOnClickListener(v -> {
            listener.createFolderClick();
            dismiss();
        });

        binding.tvSortOrder.setOnClickListener(v -> {
            listener.sortClick();
            dismiss();
        });

        binding.tvRecycleBin.setOnClickListener(v -> {
            listener.recycleBinClick();
            dismiss();
        });
    }

    public interface CategoryPopupWindowClickListener {
        void addCategoryClick();

        void sortClick();

        void recycleBinClick();
    }

    public interface FolderPopupWindowClickListener {
        void createFolderClick();

        void sortClick();

        void recycleBinClick();
    }
}
