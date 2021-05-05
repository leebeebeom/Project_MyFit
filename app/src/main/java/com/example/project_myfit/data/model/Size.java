package com.example.project_myfit.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class Size extends ParentModel{
    @PrimaryKey
    private long id;
    private int orderNumber;
    private String createdTime, modifiedTime, imageUri, brand, name, size, link, memo, parentCategory;
    private long parentId;
    private boolean isFavorite, isDeleted, parentIsDeleted;
    private Map<String, String> sizeMap;

    @Ignore
    public Size(long id, String parentCategory) {
        super(id, -1, parentCategory);
        this.id = id;
        this.parentCategory = parentCategory;
        sizeMap = new HashMap<>();
    }

    public Size(long id, int orderNumber, String createdTime, String modifiedTime, String imageUri, String brand, String name, String size, String link, String memo, long parentId, boolean isFavorite, Map<String, String> sizeMap, String parentCategory) {
        super(id, parentId, parentCategory);
        this.id = id;
        this.orderNumber = orderNumber;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.imageUri = imageUri;
        this.brand = brand;
        this.name = name;
        this.size = size;
        this.link = link;
        this.memo = memo;
        this.parentId = parentId;
        this.isFavorite = isFavorite;
        this.sizeMap = sizeMap;
        this.parentCategory = parentCategory;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public boolean getParentIsDeleted() {
        return parentIsDeleted;
    }

    public void setParentIsDeleted(boolean parentIsDeleted) {
        this.parentIsDeleted = parentIsDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size)) return false;
        Size size1 = (Size) o;
        return getId() == size1.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

