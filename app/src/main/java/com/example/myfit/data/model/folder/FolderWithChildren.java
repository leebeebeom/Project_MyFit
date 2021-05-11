package com.example.myfit.data.model.folder;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myfit.data.model.BaseModel;

import java.util.List;

public class FolderWithChildren {
    @Embedded
    private Folder folder;

    @Relation(entity = Folder.class,
            parentColumn = "id",
            entityColumn = "parentId")
    private List<Folder> childFolders;

    public boolean areChildFoldersNotEmpty() {
        return !childFolders.isEmpty();
    }

    public long[] getChildFolderIds() {
        return childFolders.stream().mapToLong(BaseModel::getId).toArray();
    }

    public Folder getFolder() {
        return folder;
    }

    public List<Folder> getChildFolders() {
        return childFolders;
    }
}
