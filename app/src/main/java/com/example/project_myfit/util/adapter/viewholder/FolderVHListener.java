package com.example.project_myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.google.android.material.checkbox.MaterialCheckBox;

public interface FolderVHListener {
    void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox);

    void onFolderItemViewLongClick(int position);

    void onFolderDragHandleTouch(RecyclerView.ViewHolder holder);
}
