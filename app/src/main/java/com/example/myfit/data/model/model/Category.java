package com.example.myfit.data.model.model;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;
import com.example.myfit.data.model.BaseModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Category extends BaseModel {
    private String name;
    private int contentsSize;

    public Category(BaseInfo baseInfo, String name) {
        super(baseInfo);
        this.name = name;
    }
}
