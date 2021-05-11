package com.example.myfit.data.model.folder;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myfit.data.model.tuple.ParentDeletedTuple;

import java.util.List;

public class FolderDeletedTuple {
    @Embedded
    private ParentDeletedTuple folderParentDeletedTuple;

    @Relation(
            parentColumn = "id",
            entityColumn = "parentId",
            entity = Folder.class)
    private List<ParentDeletedTuple> childFolderParentDeletedTuples;

    public boolean areChildFoldersNotEmpty() {
        return !childFolderParentDeletedTuples.isEmpty();
    }

    public long[] getChildFolderIds() {
        return childFolderParentDeletedTuples.stream().mapToLong(ParentDeletedTuple::getId).toArray();
    }

    public List<ParentDeletedTuple> getChildFolderParentDeletedTuples() {
        return childFolderParentDeletedTuples;
    }
}
