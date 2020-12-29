package com.example.project_myfit.ui.main.listfragment.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.ui.main.listfragment.database.ListFolder;

import java.util.List;

public class FolderDiffUtil extends DiffUtil.Callback {
    private final List<ListFolder> mOldFolderList;
    private final List<ListFolder> mNewFolderList;

    public FolderDiffUtil(List<ListFolder> oldFolderList, List<ListFolder> newFolderList) {
        mOldFolderList = oldFolderList;
        mNewFolderList = newFolderList;
    }

    @Override
    public int getOldListSize() {
        return mOldFolderList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewFolderList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldFolderList.get(oldItemPosition).getId() == mNewFolderList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ListFolder oldFolder = mOldFolderList.get(oldItemPosition);
        ListFolder newFolder = mNewFolderList.get(oldItemPosition);
        return oldFolder.getFolderName().equals(newFolder.getFolderName());
    }
}
