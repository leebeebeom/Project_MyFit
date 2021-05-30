package com.example.myfit.data.model;

import androidx.room.Embedded;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BaseModel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @Embedded
    private final BaseInfo baseInfo;

    public BaseModel(@NotNull BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
