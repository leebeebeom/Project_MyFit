package com.example.myfit.data.model.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.Size;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;

import java.util.List;
import java.util.Objects;

public class CategoryDeletedRelation {
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

    public void setCategoryDeleted(boolean isDeleted) {
        categoryDeletedTuple.setDeleted(isDeleted);
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

    public void setCategoryDeletedTuple(DeletedTuple categoryDeletedTuple) {
        this.categoryDeletedTuple = categoryDeletedTuple;
    }

    public void setChildFolderParentDeletedTuples(List<ParentDeletedTuple> childFolderParentDeletedTuples) {
        this.childFolderParentDeletedTuples = childFolderParentDeletedTuples;
    }

    public void setChildSizeParentDeletedTuples(List<ParentDeletedTuple> childSizeParentDeletedTuples) {
        this.childSizeParentDeletedTuples = childSizeParentDeletedTuples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryDeletedRelation)) return false;
        CategoryDeletedRelation that = (CategoryDeletedRelation) o;
        return getCategoryDeletedTuple().equals(that.getCategoryDeletedTuple()) &&
                getChildFolderParentDeletedTuples().equals(that.getChildFolderParentDeletedTuples()) &&
                getChildSizeParentDeletedTuples().equals(that.getChildSizeParentDeletedTuples());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategoryDeletedTuple(), getChildFolderParentDeletedTuples(), getChildSizeParentDeletedTuples());
    }
}
