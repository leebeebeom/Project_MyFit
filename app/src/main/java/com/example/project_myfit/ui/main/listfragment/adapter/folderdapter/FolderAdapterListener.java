package com.example.project_myfit.ui.main.listfragment.adapter.folderdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface FolderAdapterListener {
    void onFolderCardViewClick(Folder folder, MaterialCheckBox checkBox, int position);

    void onFolderCardViewLongClick(Folder folder, MaterialCardView cardView, MaterialCheckBox checkBox, int position);

    void onFolderDragHandTouch(RecyclerView.ViewHolder holder);
}
