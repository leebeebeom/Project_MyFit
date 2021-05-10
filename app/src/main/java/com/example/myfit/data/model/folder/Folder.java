package com.example.myfit.data.model.folder;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.DefaultInfo;

import java.util.Objects;

@Entity
public class Folder extends BaseModel {
    private String name;
    private long parentId;
    private boolean isParentDeleted;

    public Folder(DefaultInfo defaultInfo, String name, long parentId) {
        super(defaultInfo);
        this.name = name;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isParentDeleted() {
        return isParentDeleted;
    }

    public void setParentDeleted(boolean parentDeleted) {
        isParentDeleted = parentDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Folder)) return false;
        Folder folder = (Folder) o;
        return getId() == folder.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
