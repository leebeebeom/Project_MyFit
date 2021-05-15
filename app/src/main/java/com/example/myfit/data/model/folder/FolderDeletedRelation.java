package com.example.myfit.data.model.folder;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;

import java.util.List;

public class FolderDeletedRelation {
    @Embedded
    private DeletedTuple folderDeletedTuple;

    @Relation(
            parentColumn = "id",
            entityColumn = "parentId",
            entity = Folder.class)
    private List<ParentDeletedTuple> childFolderParentDeletedTuples;

    public void setFolderDeleted() {
        folderDeletedTuple.setDeleted(true);
    }

    public DeletedTuple getFolderDeletedTuple() {
        return folderDeletedTuple;
    }

    public boolean areChildFoldersNotEmpty() {
        return !childFolderParentDeletedTuples.isEmpty();
    }

    public long[] getChildFolderIds() {
        return childFolderParentDeletedTuples.stream().mapToLong(ParentDeletedTuple::getId).toArray();
    }

    public List<ParentDeletedTuple> getChildFolderParentDeletedTuples() {
        return childFolderParentDeletedTuples;
    }

    public void setFolderDeletedTuple(DeletedTuple folderDeletedTuple) {
        this.folderDeletedTuple = folderDeletedTuple;
    }

    public void setChildFolderParentDeletedTuples(List<ParentDeletedTuple> childFolderParentDeletedTuples) {
        this.childFolderParentDeletedTuples = childFolderParentDeletedTuples;
    }
}
