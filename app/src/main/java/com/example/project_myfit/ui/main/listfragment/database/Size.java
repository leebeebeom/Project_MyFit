package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Size {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int orderNumber;
    private String createdTime;
    private String modifiedTime;
    private String imageUri;
    private String brand;
    private String name;
    private String size;
    private String link;
    private String memo;
    private int folderId;
    private long inFolderId;
    private boolean isFavorite;
    private Map<String, String> sizeMap;

    @Ignore
    public Size() {
        sizeMap = new HashMap<>();
    }

    public Size(int orderNumber, String createdTime, String modifiedTime, String imageUri, String brand, String name, String size, String link, String memo, int folderId, long inFolderId, boolean isFavorite, Map<String, String> sizeMap) {
        this.orderNumber = orderNumber;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.imageUri = imageUri;
        this.brand = brand;
        this.name = name;
        this.size = size;
        this.link = link;
        this.memo = memo;
        this.folderId = folderId;
        this.inFolderId = inFolderId;
        this.isFavorite = isFavorite;
        this.sizeMap = sizeMap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public long getInFolderId() {
        return inFolderId;
    }

    public void setInFolderId(long inFolderId) {
        this.inFolderId = inFolderId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Map<String, String> getSizeMap() {
        return sizeMap;
    }

    public void setSizeMap(Map<String, String> sizeMap) {
        this.sizeMap = sizeMap;
    }
}

