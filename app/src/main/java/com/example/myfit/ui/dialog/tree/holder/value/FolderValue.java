package com.example.myfit.ui.dialog.tree.holder.value;

import com.example.myfit.data.model.folder.FolderTuple;

public class FolderValue extends BaseValue<FolderTuple> {
    private final int mMargin;

    public FolderValue(FolderTuple tuple, int margin) {
        super(tuple);
        this.mMargin = margin;
    }

    public int getMargin() {
        return mMargin;
    }
}
