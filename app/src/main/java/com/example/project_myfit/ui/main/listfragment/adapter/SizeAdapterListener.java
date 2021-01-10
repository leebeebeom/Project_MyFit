package com.example.project_myfit.ui.main.listfragment.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface SizeAdapterListener {
    void onCardViewClick(Size size, MaterialCheckBox checkBox, int position);

    void onCardViewLongClick(Size size, MaterialCheckBox checkBox, int position);

    void onDragHandTouch(RecyclerView.ViewHolder viewHolder);

    void onCheckBoxClick(Size size, MaterialCheckBox checkBox, int position);
}
