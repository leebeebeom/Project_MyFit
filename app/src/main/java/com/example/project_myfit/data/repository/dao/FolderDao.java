package com.example.project_myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_myfit.data.model.Folder;

import java.util.List;

@Dao
public interface FolderDao {
    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    LiveData<Folder[]> getFoldersLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    LiveData<Folder[]> getFoldersLive(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE id = :id")
    LiveData<Folder> getFolderLive(long id);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    Folder[] getFolders(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    Folder[] getFolders(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    Folder[] getFolders(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :parentIdDeleted")
    LiveData<String[]> getFolderNamesLive(boolean isDeleted, boolean parentIdDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted AND parentId = :parentId")
    String[] getFolderNames(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    long[] getFolderParentIds(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Folder WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    long[] getFolderParentIds(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE id = :id AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    Folder getFolder(long id, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT max(orderNumber) FROM Folder")
    int getFolderLargestOrder();

    @Insert
    long insertFolder(Folder folder);

    @Update
    void updateFolder(Folder folder);

    @Update
    void updateFolder(Folder[] folders);

    @Update
    void updateFolder(List<Folder> folders);
}
