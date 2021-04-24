package com.example.project_myfit.fragment.list.adapter.size_adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface SizeAdapterListener {
    void onSizeItemViewClick(Size size, MaterialCheckBox checkBox);

    void onSizeItemViewLongClick(int position);

    void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder);

    void onSizeFavoriteClick(Size size);
}
