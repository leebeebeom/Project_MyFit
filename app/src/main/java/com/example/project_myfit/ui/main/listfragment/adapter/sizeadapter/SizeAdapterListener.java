package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface SizeAdapterListener {
    void onListCardViewClick(Size size, MaterialCheckBox checkBox, int position);

    void onListCardViewLongClick(Size size, MaterialCheckBox checkBox, int position);

    void onListDragHandTouch(RecyclerView.ViewHolder viewHolder);

    void onListCheckBoxClick(Size size, MaterialCheckBox checkBox, int position);

    void onListCheckBoxLongCLick(int position);
}
