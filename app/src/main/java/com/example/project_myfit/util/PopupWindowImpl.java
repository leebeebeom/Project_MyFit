package com.example.project_myfit.util;

import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.project_myfit.databinding.LayoutPopupBinding;

import org.jetbrains.annotations.NotNull;

public class PopupWindowImpl extends PopupWindow {

    public PopupWindowImpl(@NotNull LayoutPopupBinding binding, PopupWindowClickListener listener) {
        super(binding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setOnClickListener(binding, listener);
    }

    private void setOnClickListener(LayoutPopupBinding binding, PopupWindowClickListener listener) {
        binding.tvAddCategory.setOnClickListener(v -> {
            listener.addCategoryClick();
            dismiss();
        });

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

    public interface PopupWindowClickListener {
        void addCategoryClick();

        void createFolderClick();

        void sortClick();

        void recycleBinClick();
    }
}
