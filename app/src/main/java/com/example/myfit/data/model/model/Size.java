package com.example.myfit.data.model.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.example.myfit.data.model.BaseModel;

import org.jetbrains.annotations.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Size extends BaseModel implements Cloneable {
    private String createdTime, modifiedTime, imageUri, brand, name, size, link, memo,
            firstInfo, secondInfo, thirdInfo, fourthInfo, fifthInfo, sixthInfo;
    private boolean isFavorite, parentDeleted;
    private long parentId;

    public Size(int parentIndex, int sortNumber, long parentId) {
        super(parentIndex, sortNumber);
        this.parentId = parentId;
    }

    @NonNull
    @NotNull
    @Override
    public Size clone() throws CloneNotSupportedException {
        return (Size) super.clone();
    }
}
