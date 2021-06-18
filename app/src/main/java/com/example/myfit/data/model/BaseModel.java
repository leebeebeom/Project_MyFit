package com.example.myfit.data.model;

import androidx.room.PrimaryKey;

import com.example.myfit.util.CommonUtil;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BaseModel {
    @PrimaryKey
    private long id;
    private final int parentIndex;
    private int sortNumber;
    private boolean deleted;
    private long deletedTime;
    private String name;

    protected BaseModel(int parentIndex, int sortNumber, String name) {
        this.id = CommonUtil.createId();
        this.parentIndex = parentIndex;
        this.sortNumber = sortNumber;
        this.name = name;
    }
}
