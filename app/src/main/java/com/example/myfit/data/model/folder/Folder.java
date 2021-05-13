package com.example.myfit.data.model.folder;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.category.Category;

@Entity
public class Folder extends Category {
    private long parentId;
    private boolean isParentDeleted;

    public Folder(BaseInfo baseInfo, String name, long parentId) {
        super(baseInfo, name);
        this.parentId = parentId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isParentDeleted() {
        return isParentDeleted;
    }

    public void setParentDeleted(boolean parentDeleted) {
        isParentDeleted = parentDeleted;
    }
}
