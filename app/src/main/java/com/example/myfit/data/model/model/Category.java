package com.example.myfit.data.model.model;

import androidx.room.Entity;

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

    public Category(int parentIndex, int orderNumber, String name) {
        super(parentIndex, orderNumber);
        this.name = name;
    }
}
