package com.example.myfit.data.model.model;

import androidx.room.Entity;

import com.example.myfit.data.model.BaseInfo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Folder extends Category {
    private long parentId;
    private boolean parentDeleted;

    public Folder(BaseInfo baseInfo, String name, long parentId) {
        super(baseInfo, name);
        this.parentId = parentId;
    }
}
