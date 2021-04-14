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
    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY folderName")
    LiveData<List<Folder>> getAllFolderLive();

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber")
    LiveData<List<Folder>> getFolderLiveByParentId(long parentId);

    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY folderName")
    List<Folder> getAllFolderList();

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Folder> getFolderListByParentCategory(String parentCategory);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Folder> getFolderListByParentId(long parentId);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 AND parentCategory = :parentCategory")
    List<String> getFolderNameListByParentCategory(String parentCategory);

    @Query("SELECT parentId FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Long> getFolderParentIdListByParentCategory(String parentCategory);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY folderName")
    List<String> getFolderNameList();

    @Query("SELECT * FROM Folder WHERE id = :id")
    Folder getFolder(long id);

    @Query("SELECT * FROM Folder ORDER BY id DESC limit 1")
    Folder getLatestFolder();

    @Query("SELECT max(orderNumber) FROM Folder")
    int getFolderLargestOrder();

    @Insert
    void folderInsert(Folder folder);

    @Update
    void folderUpdate(Folder folder);

    @Update
    void folderUpdate(List<Folder> folderList);
}
