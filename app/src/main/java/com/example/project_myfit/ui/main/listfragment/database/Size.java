package com.example.project_myfit.ui.main.listfragment.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
//TODO
//카테고리 별 필요한 사이즈 정리

@Entity
public class Size {
    @Ignore
    public static final String LENGTH = "length";
    @Ignore
    public static final String SHOULDER = "shoulder";
    @Ignore
    public static final String CHEST = "chest";
    @Ignore
    public static final String SLEEVE = "sleeve";
    @Ignore
    public static final String BRAND = "brand";
    @Ignore
    public static final String NAME = "name";
    @Ignore
    public static final String SIZE = "size";
    @Ignore
    public static final String LINK = "link";
    @Ignore
    public static final String MEMO = "memo";
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int orderNumberSize;
    private String brand;
    private String name;
    private String size;
    private String length;
    private String shoulder;
    private String chest;
    private String sleeve;
    private String link;
    private String memo;
    private int folderId;
    private boolean isFavorite;

    public Size() {

    }

    public Size(Map<String, String> dataMap, Map<String, String> sizeMap, Map<String, String> etcMap, boolean isFavorite, int folderId, int orderNumberSize) {
        this.brand = dataMap.getOrDefault(BRAND, "");
        this.name = dataMap.getOrDefault(NAME, "");
        this.size = dataMap.getOrDefault(SIZE, "");

        this.length = sizeMap.getOrDefault(LENGTH, "");
        this.shoulder = sizeMap.getOrDefault(CHEST, "");
        this.chest = sizeMap.getOrDefault(SHOULDER, "");
        this.sleeve = sizeMap.getOrDefault(SLEEVE, "");

        this.link = etcMap.getOrDefault(LINK, "");
        this.memo = etcMap.getOrDefault(MEMO, "");

        this.isFavorite = isFavorite;
        this.folderId = folderId;

        this.orderNumberSize = orderNumberSize;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getShoulder() {
        return shoulder;
    }

    public void setShoulder(String shoulder) {
        this.shoulder = shoulder;
    }

    public String getChest() {
        return chest;
    }

    public void setChest(String chest) {
        this.chest = chest;
    }

    public String getSleeve() {
        return sleeve;
    }

    public void setSleeve(String sleeve) {
        this.sleeve = sleeve;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public int getOrderNumberSize() {
        return orderNumberSize;
    }

    public void setOrderNumberSize(int orderNumberSize) {
        this.orderNumberSize = orderNumberSize;
    }

    @NotNull
    @Override
    public String toString() {
        return "TopSize{" +
                "id=" + id +
                ", brandName='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", length='" + length + '\'' +
                ", shoulder='" + shoulder + '\'' +
                ", chest='" + chest + '\'' +
                ", sleeve='" + sleeve + '\'' +
                ", link='" + link + '\'' +
                ", memo='" + memo + '\'' +
                ", folderId=" + folderId +
                ", isFavorite=" + isFavorite +
                '}';
    }
}

