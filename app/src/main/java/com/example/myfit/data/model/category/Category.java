package com.example.myfit.data.model.category;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.BaseModel;

@Entity
public class Category extends BaseModel {
    private String name;
    private int contentsSize;

    public Category(BaseInfo baseInfo, String name) {
        super(baseInfo);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getContentsSize() {
        return contentsSize;
    }

    public void setContentsSize(int contentsSize) {
        this.contentsSize = contentsSize;
    }
}
