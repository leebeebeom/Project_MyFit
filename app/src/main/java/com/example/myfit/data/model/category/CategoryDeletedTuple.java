package com.example.myfit.data.model.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.Size;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;

import java.util.List;

public class CategoryDeletedTuple {
    @Embedded
    private DeletedTuple categoryDeletedTuple;

    @Relation(
            parentColumn = "id",
            entityColumn = "parentId",
            entity = Folder.class)
    private List<ParentDeletedTuple> childFolderParentDeletedTuples;

    @Relation(
            parentColumn = "id",
            entityColumn = "parentId",
            entity = Size.class)
    private List<ParentDeletedTuple> childSizeParentDeletedTuples;

    public DeletedTuple getCategoryDeletedTuple() {
        return categoryDeletedTuple;
    }

    public void setCategoryDeleted() {
        categoryDeletedTuple.setDeleted(true);
    }

    public long[] getChildFolderIds() {
        return childFolderParentDeletedTuples.stream().mapToLong(ParentDeletedTuple::getId).toArray();
    }

    public boolean areChildFoldersNotEmpty() {
        return !childFolderParentDeletedTuples.isEmpty();
    }

    public boolean areChildSizesNotEmpty() {
        return !childSizeParentDeletedTuples.isEmpty();
    }

    public List<ParentDeletedTuple> getChildFolderParentDeletedTuples() {
        return childFolderParentDeletedTuples;
    }

    public List<ParentDeletedTuple> getChildSizeParentDeletedTuples() {
        return childSizeParentDeletedTuples;
    }
}
