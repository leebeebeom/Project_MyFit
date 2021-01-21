package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface SizeAdapterListener {
    void onSizeCardViewClick(Size size, MaterialCheckBox checkBox, int position);

    void onSizeCardViewLongClick(int position);

    void onSizeDragHandTouch(RecyclerView.ViewHolder viewHolder);

    void onSizeCheckBoxClick(Size size, MaterialCheckBox checkBox, int position);

    void onSizeCheckBoxLongCLick(int position);
}
