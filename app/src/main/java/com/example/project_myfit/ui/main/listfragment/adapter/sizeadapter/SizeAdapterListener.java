package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface SizeAdapterListener {
    void onSizeCardViewClick(Size size, MaterialCheckBox checkBox, int position);

    void onSizeCardViewLongClick(MaterialCardView cardView, int position);

    void onSizeDragHandleTouch(RecyclerView.ViewHolder viewHolder, MotionEvent event);
}
