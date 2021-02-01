package com.example.project_myfit.ui.main.listfragment.adapter.folderdapter;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface FolderAdapterListener {
    void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox, int position);

    void onFolderItemViewLongClick(MaterialCardView cardView, int position);

    void onFolderDragHandleTouch(RecyclerView.ViewHolder holder, LinearLayoutCompat folderAmountLayout);
}
