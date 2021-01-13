package com.example.project_myfit.ui.main.listfragment.adapter.folderdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface FolderAdapterListener {
    void onCardViewClick(Folder folder, MaterialCheckBox checkBox, int position);

    void onCardViewLongClick(Folder folder, RecyclerView.ViewHolder holder, MaterialCheckBox checkBox, int position);

    void onDragHandTouch(RecyclerView.ViewHolder holder);
}
