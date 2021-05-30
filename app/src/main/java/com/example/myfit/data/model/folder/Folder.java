package com.example.myfit.data.model.folder;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.category.Category;

import java.util.Objects;

@Entity
public class Folder extends Category {
    private long parentId;
    private boolean parentDeleted;

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
        return parentDeleted;
    }

    public void setParentDeleted(boolean parentDeleted) {
        this.parentDeleted = parentDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Folder)) return false;
        if (!super.equals(o)) return false;
        Folder folder = (Folder) o;
        return getParentId() == folder.getParentId() &&
                isParentDeleted() == folder.isParentDeleted();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParentId(), isParentDeleted());
    }
}
