package com.leebeebeom.closetnote.data.tuple.tuple;

import com.leebeebeom.closetnote.data.model.model.Folder;

import lombok.Getter;

@Getter
public class FolderTuple2 {
    private final long id;
    private final String name;
    private final boolean isDeleted, isParentDeleted;

    public FolderTuple2(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.isDeleted = folder.isDeleted();
        this.isParentDeleted = folder.isParentDeleted();
    }
}
