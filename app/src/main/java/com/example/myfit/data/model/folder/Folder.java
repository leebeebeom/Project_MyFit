package com.example.myfit.data.model.folder;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.BaseInfo;

@Entity
public class Folder extends BaseModel {
    private String name;
    private long parentId;
    private boolean isParentDeleted;

    public Folder(BaseInfo baseInfo, String name, long parentId) {
        super(baseInfo);
        this.name = name;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
