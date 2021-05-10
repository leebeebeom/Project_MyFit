package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.Folder;

import java.util.List;

@Dao
public interface FolderDao {
    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND isParentDeleted = 0")
    LiveData<List<Folder>> getFoldersLive();

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    LiveData<List<Folder>> getFoldersLiveByParentId(long parentId);

    @Query("SELECT * FROM Folder WHERE id = :id")
    LiveData<Folder> getFolderLiveById(long id);

    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND isParentDeleted = 0")
    List<Folder> getFolders();

    @Query("SELECT * FROM Folder WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0 AND isParentDeleted = 0")
    List<Folder> getFoldersByParentCategory(int parentCategoryIndex);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    List<Folder> getFoldersByParentId(long parentId);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = 0 AND isParentDeleted = 0")
    LiveData<List<String>> getFolderNamesLive();

    @Query("SELECT folderName FROM Folder WHERE isDeleted = 0 AND isParentDeleted = 0 AND parentId = :parentId")
    List<String> getFolderNamesByParentId(long parentId);

    @Query("SELECT parentId FROM Folder WHERE isDeleted = 0 AND isParentDeleted = 0")
    List<Long> getFolderParentIds();

    @Query("SELECT parentId FROM Folder WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0 AND isParentDeleted = 0")
    List<Long> getFolderParentIdsByParentCategory(int parentCategoryIndex);

    @Query("SELECT * FROM Folder WHERE id = :id AND isDeleted = 0 AND isParentDeleted = 0")
    Folder getFolderById(long id);

    @Query("SELECT max(orderNumber) FROM Folder")
    int getFolderLargestOrder();

    @Insert
    long insertFolder(Folder folder);

    @Update
    void updateFolder(Folder folder);

    @Update
    void updateFolder(List<Folder> folders);
}
