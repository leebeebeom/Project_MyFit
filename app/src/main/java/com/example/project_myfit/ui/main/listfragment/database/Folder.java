package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Folder {
    @PrimaryKey
    private long id;
    private long folderId;
    private String folderName, parentCategory;
    private int orderNumber, isDeleted, dummy;

    public Folder(long id, String folderName, long folderId, int orderNumber, String parentCategory) {
        this.id = id;
        this.folderName = folderName;
        this.folderId = folderId;
        this.orderNumber = orderNumber;
        this.dummy = 0;
        this.parentCategory = parentCategory;
        this.isDeleted = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getDummy() {
        return dummy;
    }

    public void setDummy(int dummy) {
        this.dummy = dummy;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
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
