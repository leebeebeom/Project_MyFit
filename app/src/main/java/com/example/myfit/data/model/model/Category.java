package com.example.myfit.data.model.model;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.example.myfit.data.model.BaseModel;

import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@Setter
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseModel {
    @Ignore
    private int folderContentSize;
    @Ignore
    private int sizeContentSize;

    public Category(int parentIndex, int sortNumber, String name) {
        super(parentIndex, sortNumber, name);
    }

    public static Category getDummy() {
        return new Category(-1, -1, "");
    }

    public int getContentSize() {
        return folderContentSize + sizeContentSize;
    }
}
