package com.example.project_myfit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_myfit.data.model.Folder;

import java.util.List;

@Dao
public interface FolderDao {
    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ORDER BY folderName")
    LiveData<List<Folder>> getFolderLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ORDER BY orderNumber")
    LiveData<List<Folder>> getFolderLive(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ORDER BY orderNumber")
    List<Folder> getFolderList(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    List<Folder> getFolderList(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    List<Folder> getFolderList(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :parentIdDeleted")
    LiveData<List<String>> getFolderNameLive(boolean isDeleted, boolean parentIdDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted AND parentId = :parentId")
    List<String> getFolderNameList(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    List<Long> getFolderParentIdList(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Folder WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    List<Long> getFolderParentIdList(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder WHERE id = :id")
    LiveData<Folder> getSingleFolderLive(long id);

    @Query("SELECT * FROM Folder WHERE id = :id AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    Folder getFolder(long id, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Folder ORDER BY id DESC limit 1")
    Folder getLatestFolder();

    @Query("SELECT max(orderNumber) FROM Folder")
    int getFolderLargestOrder();

    @Insert
    void insertFolder(Folder folder);

    @Update
    void updateFolder(Folder folder);

    @Update
    void updateFolder(List<Folder> folderList);
}
