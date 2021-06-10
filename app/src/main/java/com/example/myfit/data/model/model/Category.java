package com.example.myfit.data.model.model;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseModel {
    private String name;
    private int contentSize;

    public Category(int parentIndex, int sortNumber, String name) {
        super(parentIndex, sortNumber);
        this.name = name;
    }
}
