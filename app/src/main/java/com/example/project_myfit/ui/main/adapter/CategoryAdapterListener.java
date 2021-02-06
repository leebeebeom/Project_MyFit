package com.example.project_myfit.ui.main.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.database.Category;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface CategoryAdapterListener {
    void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox, int position, int viewPagerPosition);

    void onCategoryCardViewLongClick(MaterialCardView cardView, int position);

    void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder, int viewPagerPosition);
}
