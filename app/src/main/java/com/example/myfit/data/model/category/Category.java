package com.example.myfit.data.model.category;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.DefaultInfo;

@Entity
public class Category extends BaseModel {
    private String name;

    public Category(DefaultInfo defaultInfo, String name) {
        super(defaultInfo);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
