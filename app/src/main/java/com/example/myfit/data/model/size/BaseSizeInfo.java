package com.example.myfit.data.model.size;

import java.util.Objects;

public class BaseSizeInfo {
    private String createdTime, modifiedTime, imageUri, brand, name, size, link, memo;
    private boolean isFavorite;

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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseSizeInfo)) return false;
        BaseSizeInfo that = (BaseSizeInfo) o;
        return isFavorite() == that.isFavorite() &&
                getCreatedTime().equals(that.getCreatedTime()) &&
                Objects.equals(getModifiedTime(), that.getModifiedTime()) &&
                Objects.equals(getImageUri(), that.getImageUri()) &&
                getBrand().equals(that.getBrand()) &&
                getName().equals(that.getName()) &&
                Objects.equals(getSize(), that.getSize()) &&
                Objects.equals(getLink(), that.getLink()) &&
                Objects.equals(getMemo(), that.getMemo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreatedTime(), getModifiedTime(), getImageUri(), getBrand(), getName(), getSize(), getLink(), getMemo(), isFavorite());
    }
}
