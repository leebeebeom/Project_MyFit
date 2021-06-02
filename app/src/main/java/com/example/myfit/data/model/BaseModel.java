package com.example.myfit.data.model;

import androidx.room.PrimaryKey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BaseModel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final int parentIndex;
    private int sortNumber;
    private boolean deleted;
    private long deletedTime;

    protected BaseModel(int parentIndex, int sortNumber) {
        this.parentIndex = parentIndex;
        this.sortNumber = sortNumber;
    }
}
