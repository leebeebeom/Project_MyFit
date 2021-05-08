package com.example.project_myfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Folder extends ParentModel {
    @PrimaryKey
    private final long id;
    private final int parentCategoryIndex;
    private long parentId;
    private String folderName;
    private int orderNumber;
    private boolean isDeleted, dummy, isParentDeleted;

    public Folder(long id, String folderName, long parentId, int orderNumber, int parentCategoryIndex) {
        super(id, parentId, parentCategoryIndex);
        this.id = id;
        this.folderName = folderName;
        this.parentId = parentId;
        this.orderNumber = orderNumber;
        this.parentCategoryIndex = parentCategoryIndex;
    }

    public long getId() {
        return id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean getDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getParentCategoryIndex() {
        return parentCategoryIndex;
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
