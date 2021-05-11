package com.example.myfit.data.model.category;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.BaseSize;

import java.util.List;

public class CategoryWithChildren {
    @Embedded
    private Category category;

    @Relation(
            entity = Category.class,
            parentColumn = "id",
            entityColumn = "parentId")
    private List<Folder> childFolders;

    @Relation(
            entity = Category.class,
            parentColumn = "id",
            entityColumn = "parentId")
    private List<BaseSize> childSizes;

    public Category getCategory() {
        return category;
    }

    public void setCategoryDeleted() {
        category.setDeleted(true);
    }

    public long[] getChildFolderIds() {
        return childFolders.stream().mapToLong(Folder::getId).toArray();
    }

    public boolean areChildFoldersNotEmpty() {
        return !childFolders.isEmpty();
    }

    public boolean areChildSizesNotEmpty() {
        return !childSizes.isEmpty();
    }

    public List<Folder> getChildFolders() {
        return childFolders;
    }

    public List<BaseSize> getChildSizes() {
        return childSizes;
    }
}
