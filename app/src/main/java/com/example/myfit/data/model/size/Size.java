package com.example.myfit.data.model.size;

import androidx.room.Embedded;
import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.BaseModel;

@Entity
public class Size extends BaseModel {
    @Embedded
    private final BaseSizeInfo baseSizeInfo;
    @Embedded
    private final DetailSizeInfo detailSizeInfo;
    private long parentId;
    private boolean isParentDeleted;

    public Size(BaseInfo baseInfo, long parentId) {
        super(baseInfo);
        this.parentId = parentId;
        this.baseSizeInfo = new BaseSizeInfo();
        this.detailSizeInfo = new DetailSizeInfo();
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isParentDeleted() {
        return isParentDeleted;
    }

    public void setParentDeleted(boolean parentDeleted) {
        this.isParentDeleted = parentDeleted;
    }

    public String getCreatedTime() {
        return baseSizeInfo.getCreatedTime();
    }

    public void setCreatedTime(String createdTime) {
        this.baseSizeInfo.setCreatedTime(createdTime);
    }

    public String getModifiedTime() {
        return baseSizeInfo.getModifiedTime();
    }

    public void setModifiedTime(String modifiedTime) {
        this.baseSizeInfo.setModifiedTime(modifiedTime);
    }

    public String getImageUri() {
        return baseSizeInfo.getImageUri();
    }

    public void setImageUri(String imageUri) {
        this.baseSizeInfo.setImageUri(imageUri);
    }

    public String getBrand() {
        return baseSizeInfo.getBrand();
    }

    public void setBrand(String brand) {
        this.baseSizeInfo.setBrand(brand);
    }

    public String getName() {
        return baseSizeInfo.getName();
    }

    public void setName(String name) {
        this.baseSizeInfo.setName(name);
    }

    public String getSize() {
        return baseSizeInfo.getSize();
    }

    public void setSize(String size) {
        this.baseSizeInfo.setSize(size);
    }

    public String getLink() {
        return baseSizeInfo.getLink();
    }

    public void setLink(String link) {
        this.baseSizeInfo.setLink(link);
    }

    public String getMemo() {
        return baseSizeInfo.getMemo();
    }

    public void setMemo(String memo) {
        this.baseSizeInfo.setMemo(memo);
    }

    public boolean isFavorite() {
        return baseSizeInfo.isFavorite();
    }

    public void setFavorite(boolean favorite) {
        this.baseSizeInfo.setFavorite(favorite);
    }

    public String getFirstInfo() {
        return detailSizeInfo.getFirstInfo();
    }

    public void setFirstInfo(String firstInfo) {
        this.detailSizeInfo.setFirstInfo(firstInfo);
    }

    public String getSecondInfo() {
        return detailSizeInfo.getSecondInfo();
    }

    public void setSecondInfo(String secondInfo) {
        this.detailSizeInfo.setSecondInfo(secondInfo);
    }

    public String getThirdInfo() {
        return detailSizeInfo.getThirdInfo();
    }

    public void setThirdInfo(String thirdInfo) {
        this.detailSizeInfo.setThirdInfo(thirdInfo);
    }

    public String getFourthInfo() {
        return detailSizeInfo.getFourthInfo();
    }

    public void setFourthInfo(String fourthInfo) {
        this.detailSizeInfo.setFourthInfo(fourthInfo);
    }

    public String getFifthInfo() {
        return detailSizeInfo.getFifthInfo();
    }

    public void setFifthInfo(String fifthInfo) {
        this.detailSizeInfo.setFirstInfo(fifthInfo);
    }

    public String getSixthInfo() {
        return detailSizeInfo.getSixthInfo();
    }

    public void setSixthInfo(String sixthInfo) {
        this.detailSizeInfo.setSixthInfo(sixthInfo);
    }
}
