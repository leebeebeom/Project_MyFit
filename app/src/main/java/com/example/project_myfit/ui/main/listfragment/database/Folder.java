package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Folder {
    @PrimaryKey
    private long id;
    private String folderName;
    private int folderId;
    private int orderNumber;
    private String itemAmount;

    public Folder(long id, String folderName, int folderId, int orderNumber, String itemAmount) {
        this.id = id;
        this.folderName = folderName;
        this.folderId = folderId;
        this.orderNumber = orderNumber;
        this.itemAmount = itemAmount;
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


    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    @Override
    public String toString() {
        return "ListFolder{" +
                "id=" + id +
                ", folderName='" + folderName + '\'' +
                ", folderId=" + folderId +
                ", orderNumberFolder=" + orderNumber +
                ", itemCount=" + itemAmount +
                '}';
    }
}
