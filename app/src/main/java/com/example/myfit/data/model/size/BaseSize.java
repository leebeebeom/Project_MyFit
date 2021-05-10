package com.example.myfit.data.model.size;

import androidx.room.Embedded;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.DefaultInfo;

public class BaseSize extends BaseModel {
    @Embedded
    private final DefaultSizeInfo defaultSizeInfo;
    private long parentId;
    private boolean isParentDeleted;

    public BaseSize(DefaultInfo defaultInfo, long parentId) {
        super(defaultInfo);
        this.parentId = parentId;
        this.defaultSizeInfo = new DefaultSizeInfo();
    }

    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isParentDeleted() {
        return this.isParentDeleted;
    }

    public void setParentDeleted(boolean parentDeleted) {
        this.isParentDeleted = parentDeleted;
    }

    public String getCreatedTime() {
        return defaultSizeInfo.getCreatedTime();
    }

    public void setCreatedTime(String createdTime) {
        this.defaultSizeInfo.setCreatedTime(createdTime);
    }

    public String getModifiedTime() {
        return defaultSizeInfo.getModifiedTime();
    }

    public void setModifiedTime(String modifiedTime) {
        this.defaultSizeInfo.setModifiedTime(modifiedTime);
    }

    public String getImageUri() {
        return defaultSizeInfo.getImageUri();
    }

    public void setImageUri(String imageUri) {
        this.defaultSizeInfo.setImageUri(imageUri);
    }

    public String getBrand() {
        return defaultSizeInfo.getBrand();
    }

    public void setBrand(String brand) {
        this.defaultSizeInfo.setBrand(brand);
    }

    public String getName() {
        return defaultSizeInfo.getName();
    }

    public void setName(String name) {
        this.defaultSizeInfo.setName(name);
    }

    public String getSize() {
        return defaultSizeInfo.getSize();
    }

    public void setSize(String size) {
        this.defaultSizeInfo.setSize(size);
    }

    public String getLink() {
        return defaultSizeInfo.getLink();
    }

    public void setLink(String link) {
        this.defaultSizeInfo.setLink(link);
    }

    public String getMemo() {
        return defaultSizeInfo.getMemo();
    }

    public void setMemo(String memo) {
        this.defaultSizeInfo.setMemo(memo);
    }

    public boolean isFavorite() {
        return defaultSizeInfo.isFavorite();
    }

    public void setFavorite(boolean favorite) {
        this.defaultSizeInfo.setFavorite(favorite);
    }
}
