package com.example.project_myfit.ui.main.listfragment.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.util.List;

public class SizeDiffUtil extends DiffUtil.Callback {
    private List<Size> mOldSizeList;
    private List<Size> mNewSizeList;

    public SizeDiffUtil(List<Size> oldSizeList, List<Size> newSizeList) {
        mOldSizeList = oldSizeList;
        mNewSizeList = newSizeList;
    }

    @Override
    public int getOldListSize() {
        return mOldSizeList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewSizeList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldSizeList.get(oldItemPosition).getId() == mNewSizeList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Size oldSize = mOldSizeList.get(oldItemPosition);
        Size newSize = mOldSizeList.get(newItemPosition);
        return false;
    }
}
