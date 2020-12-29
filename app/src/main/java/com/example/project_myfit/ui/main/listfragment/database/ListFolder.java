package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ListFolder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String folderName;
    private int folderId;
    private int orderNumberFolder;
    private int itemCount;

    public ListFolder(String folderName, int folderId, int orderNumberFolder, int itemCount) {
        this.folderName = folderName;
        this.folderId = folderId;
        this.orderNumberFolder = orderNumberFolder;
        this.itemCount = itemCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getOrderNumberFolder() {
        return orderNumberFolder;
    }

    public void setOrderNumberFolder(int orderNumberFolder) {
        this.orderNumberFolder = orderNumberFolder;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return "ListFolder{" +
                "id=" + id +
                ", folderName='" + folderName + '\'' +
                ", folderId=" + folderId +
                ", orderNumberFolder=" + orderNumberFolder +
                ", itemCount=" + itemCount +
                '}';
    }
}
