package com.example.project_myfit.util.adapter.view_holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface SizeVHListener {
    void onSizeItemViewClick(Size size, MaterialCheckBox checkBox);

    void onSizeItemViewLongClick(int position);

    void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder);

    void onSizeFavoriteClick(Size size);
}
