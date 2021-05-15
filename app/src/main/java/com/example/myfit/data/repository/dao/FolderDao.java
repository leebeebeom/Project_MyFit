package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.folder.FolderDeletedRelation;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;
import com.example.myfit.util.constant.SortValue;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

@Dao
public abstract class FolderDao extends BaseDao<Folder, CategoryFolderTuple> {
    private final AppDataBase appDataBase;
    private SizeDao sizeDao;

    @Inject
    public FolderDao(@NotNull AppDataBase appDataBase) {
        this.appDataBase = appDataBase;
    }

    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedFolderTuplesLive() {
        LiveData<List<CategoryFolderTuple>> folderTuplesLive = this.getDeletedFolderTuplesLive();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(folderTuplesLive, true);

        return this.getClassifiedFolderTuplesLive(folderTuplesLive, contentsSizesLive, SortValue.SORT_DELETED.getValue());
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE isDeleted = 1 AND isParentDeleted = 0")
    protected abstract LiveData<List<CategoryFolderTuple>> getDeletedFolderTuplesLive();

    @NotNull
    private LiveData<List<List<CategoryFolderTuple>>> getClassifiedFolderTuplesLive(LiveData<List<CategoryFolderTuple>> folderTuplesLive,
                                                                                    LiveData<int[]> contentsSizesLive,
                                                                                    int sort) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<CategoryFolderTuple> folderTuples = folderTuplesLive.getValue();
            super.setContentsSize(folderTuples, contentsSizes);
            List<List<CategoryFolderTuple>> classifiedList = super.getClassifiedListByParentIndex(folderTuples);
            return super.orderCategoryFolderTuplesList(sort, classifiedList);
        });
    }


    //to list
    public LiveData<List<CategoryFolderTuple>> getFolderTuplesLiveByParentId(long parentId, int sort) {
        LiveData<List<CategoryFolderTuple>> folderTuplesLive = this.getFolderTuplesLiveByParentId2(parentId);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(folderTuplesLive, false);

        return this.getFolderTuplesLive(folderTuplesLive, contentsSizesLive, sort);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract LiveData<List<CategoryFolderTuple>> getFolderTuplesLiveByParentId2(long parentId);

    @NotNull
    private LiveData<List<CategoryFolderTuple>> getFolderTuplesLive(LiveData<List<CategoryFolderTuple>> folderTuplesLive,
                                                                    LiveData<int[]> contentsSizesLive,
                                                                    int sort) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<CategoryFolderTuple> folderTuples = folderTuplesLive.getValue();
            super.setContentsSize(folderTuples, contentsSizes);
            return super.orderCategoryFolderTuples(sort, folderTuples);
        });
    }

    //to searchView
    public LiveData<List<List<CategoryFolderTuple>>> getSearchFolderTuplesListLive(String keyWord, int sort) {
        LiveData<List<CategoryFolderTuple>> folderTuplesLive = getSearchFolderTuplesLive(false, keyWord);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(folderTuplesLive, false);

        return this.getClassifiedFolderTuplesLive(folderTuplesLive, contentsSizesLive, sort);
    }

    //to recycleBin search
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchFolderTuplesListLive(String keyWord) {
        LiveData<List<CategoryFolderTuple>> folderTuplesLive = getSearchFolderTuplesLive(true, keyWord);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(folderTuplesLive, true);

        return this.getClassifiedFolderTuplesLive(folderTuplesLive, contentsSizesLive, SortValue.SORT_DELETED.getValue());
    }


    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE isDeleted = :isDeleted AND isParentDeleted = 0 AND name LIKE :keyWord")
    protected abstract LiveData<List<CategoryFolderTuple>> getSearchFolderTuplesLive(boolean isDeleted, String keyWord);

    //to treeView (disposable)
    @Transaction
    public List<CategoryFolderTuple> getFolderTuplesByParentIndex(byte parentIndex) {
        List<CategoryFolderTuple> folderTuples = this.getFolderTuplesByParentIndex2(parentIndex);
        long[] folderIds = super.getItemIds(folderTuples);
        int[] folderContentsSize = getContentsSizesByParentIds(folderIds);
        super.setContentsSize(folderTuples, folderContentsSize);
        return folderTuples;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE parentIndex = :parentIndex AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract List<CategoryFolderTuple> getFolderTuplesByParentIndex2(byte parentIndex);

    @Transaction
    //to treeView
    public CategoryFolderTuple getFolderTupleById(long id) {
        CategoryFolderTuple folderTuple = this.getFolderTupleById2(id);
        int contentsSize = getContentsSizeByParentId(id);
        folderTuple.setContentsSize(contentsSize);
        return folderTuple;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE id = :id AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract CategoryFolderTuple getFolderTupleById2(long id);

    //to list
    @Query("SELECT * FROM Folder WHERE id = :id")
    public abstract LiveData<Folder> getFolderLiveById(long id);

    public long insertFolder(String name, long parentId, byte parentIndex) {
        int orderNumber = this.getFolderLargestOrder() + 1;
        Folder folder = ModelFactory.makeFolder(name, parentId, parentIndex, orderNumber);
        return insert(folder);
    }

    @Query("SELECT max(orderNumber) FROM Folder")
    protected abstract int getFolderLargestOrder();

    public void updateFolder(long id, String name) {
        CategoryFolderTuple folderTuple = this.getFolderTupleById(id);
        folderTuple.setName(name);
        this.updateTuple(folderTuple);
    }

    @Transaction
    public void moveFolders(long targetId, long[] folderIds) {
        ParentIdTuple[] folderParentIdTuples = this.getFolderParentIdTuplesByIds(folderIds);
        Arrays.stream(folderParentIdTuples)
                .forEach(parentIdTuple -> parentIdTuple.setParentId(targetId));
        this.updateParentIdTuples(folderParentIdTuples);
    }

    @Query("SELECT id, parentId FROM Folder WHERE id IN (:ids)")
    protected abstract ParentIdTuple[] getFolderParentIdTuplesByIds(long[] ids);


    @Transaction
    public void deleteOrRestoreFolders(long[] folderIds, boolean isDeleted) {
        FolderDeletedRelation[] folderDeletedRelations = this.getFolderDeletedRelationsByIds(folderIds, !isDeleted);
        DeletedTuple[] folderDeletedTuples = super.getFolderDeletedTuples(folderDeletedRelations);
        super.setDeletedTuples(folderDeletedTuples, isDeleted);
        this.updateDeletedTuples(folderDeletedTuples);

        this.setChildrenParentDeleted(folderDeletedRelations, isDeleted);
    }

    private void setChildrenParentDeleted(FolderDeletedRelation[] folderDeletedTupleWithChildren, boolean isDeleted) {
        if (sizeDao == null) sizeDao = appDataBase.sizeDao();

        LinkedList<ParentDeletedTuple> folderChildParentDeletedTuples = this.getFolderChildFolderParentDeletedTuples(folderDeletedTupleWithChildren);
        long[] folderChildFolderIds = super.getParentTuplesIds(folderChildParentDeletedTuples);
        //folderChildParentDeletedTuples -> allChildFolderParentDeletedTuples
        this.addAllChildParentDeletedTuples(folderChildFolderIds, folderChildParentDeletedTuples, isDeleted);

        long[] allFolderIds = super.getParentTuplesIds(folderChildParentDeletedTuples);
        List<ParentDeletedTuple> allChildSizeParentDeletedTuple = sizeDao.getSizeParentDeletedTuplesByParentIds(allFolderIds, !isDeleted);

        super.setParentDeletedTuples(folderChildParentDeletedTuples, isDeleted);
        super.setParentDeletedTuples(allChildSizeParentDeletedTuple, isDeleted);
        this.updateParentDeletedTuples(folderChildParentDeletedTuples);
        sizeDao.updateParentDeletedTuples(allChildSizeParentDeletedTuple);
    }

    private void addAllChildParentDeletedTuples(long[] folderChildFolderIds,
                                                @NotNull LinkedList<ParentDeletedTuple> folderChildParentDeletedTuples,
                                                boolean isDeletedDenial) {
        FolderDeletedRelation[] folderChildFolderDeletedTupleWithChildren = this.getFolderDeleteRelationByParentIds(folderChildFolderIds, isDeletedDenial);

        LinkedList<ParentDeletedTuple> folderChildFolderDeletedTuples = super.getFolderChildFolderParentDeletedTuples(folderChildFolderDeletedTupleWithChildren);
        folderChildParentDeletedTuples.addAll(folderChildFolderDeletedTuples);

        Arrays.stream(folderChildFolderDeletedTupleWithChildren)
                .filter(FolderDeletedRelation::areChildFoldersNotEmpty)
                .forEach(folderDeletedRelation -> addAllChildParentDeletedTuples(folderDeletedRelation.getChildFolderIds(),
                        folderChildParentDeletedTuples, isDeletedDenial));
    }

    @Query("SELECT id, isDeleted FROM Folder WHERE id IN (:ids) AND isDeleted = :isDeleted AND isParentDeleted = 0")
    protected abstract FolderDeletedRelation[] getFolderDeletedRelationsByIds(long[] ids, boolean isDeleted);

    @Query("SELECT id, isDeleted FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = :isParentDeleted")
    protected abstract FolderDeletedRelation[] getFolderDeleteRelationByParentIds(long[] parentIds, boolean isParentDeleted);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateTuple(CategoryFolderTuple folderTuple);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    public abstract void updateTuples(List<CategoryFolderTuple> folderTuple);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateDeletedTuples(DeletedTuple[] deletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateParentIdTuples(ParentIdTuple[] parentIdTuples);

    //from addFolder dialog
    @Query("SELECT EXISTS(SELECT name, parentId FROM Folder WHERE name =:folderName AND parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0)")
    public abstract boolean isExistingFolderName(String folderName, long parentId);

    public interface FolderDaoInterface {
        LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedFolderTuplesLive();

        LiveData<List<CategoryFolderTuple>> getFolderTuplesLiveByParentId(long parentId);

        LiveData<List<List<CategoryFolderTuple>>> getSearchFolderTuplesListLive(String keyWord);

        LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchFolderTuplesListLive(String keyWord);

        List<CategoryFolderTuple> getFolderTuplesByParentIndex(byte parentIndex);

        CategoryFolderTuple getFolderTupleById(long id);

        LiveData<Folder> getFolderLiveById(long id);

        long insertFolder(String name, long parentId, byte parentIndex);

        void updateFolder(long id, String name);

        void updateFolders(List<CategoryFolderTuple> folderTuple);

        void moveFolders(long targetId, long[] folderIds);

        void deleteOrRestoreFolders(long[] folderIds, boolean isDeleted);

        boolean isExistingFolderName(String folderName, long parentId);
    }
}
