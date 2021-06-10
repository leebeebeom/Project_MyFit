package com.example.myfit.data.model.model;

import androidx.room.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Folder extends Category {
    private long parentId;
    private boolean parentDeleted;

    public Folder(int parentIndex, int sortNumber, String name, long parentId) {
        super(parentIndex, sortNumber, name);
        this.parentId = parentId;
    }
}
