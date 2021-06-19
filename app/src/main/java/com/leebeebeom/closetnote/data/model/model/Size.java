package com.leebeebeom.closetnote.data.model.model;

import androidx.room.Entity;

import com.leebeebeom.closetnote.data.model.BaseModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Size extends BaseModel {
    private String createdTime, modifiedTime, imageUri, brand, name, size, link, memo,
            firstInfo, secondInfo, thirdInfo, fourthInfo, fifthInfo, sixthInfo;
    private boolean favorite, parentDeleted;
    private long parentId;

    public Size(int parentIndex, int sortNumber, long parentId, String name) {
        super(parentIndex, sortNumber, name);
        this.parentId = parentId;
    }
}
