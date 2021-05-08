package com.example.project_myfit.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.project_myfit.data.dao.CategoryDao;
import com.example.project_myfit.data.dao.FolderDao;
import com.example.project_myfit.data.dao.RecentSearchDao;
import com.example.project_myfit.data.dao.SizeDao;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.RecentSearch;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.util.CommonUtil;
import com.example.project_myfit.util.Constant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Category.class, Size.class, Folder.class, RecentSearch.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {
    private static final Migration MIGRATION_2_1 = new Migration(2, 1) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE Category_new(" +
                    "id INTEGER NOT NULL PRIMARY KEY," +
                    "parentCategory INTEGER NOT NULL DEFAULT 0," +
                    "orderNumber INTEGER NOT NULL," +
                    "isDeleted INTEGER NOT NULL," +
                    "dummy INTEGER NOT NULL," +
                    "categoryName TEXT," +
                    "parentId INTEGER NOT NULL)");
            database.execSQL("INSERT INTO Category_new (id, orderNumber, isDeleted, dummy, categoryName, parentId)" +
                    "SELECT id, orderNumber, isDeleted, dummy ,categoryName, parentId FROM Category");
            database.execSQL("DROP TABLE Category");
            database.execSQL("ALTER TABLE Category_new RENAME TO Category");

            database.execSQL("CREATE TABLE Folder_new(" +
                    "id INTEGER NOT NULL PRIMARY KEY," +
                    "parentCategory INTEGER NOT NULL DEFAULT 0," +
                    "parentId INTEGER NOT NULL," +
                    "folderName TEXT," +
                    "orderNumber INTEGER NOT NULL, " +
                    "isDeleted INTEGER NOT NULL," +
                    "dummy INTEGER NOT NULL," +
                    "isParentDeleted INTEGER NOT NULL)");
            database.execSQL("INSERT INTO Folder_new (id, parentId, folderName, orderNumber, isDeleted, dummy ,isParentDeleted)" +
                    "SELECT id, parentId, folderName, orderNumber, isDeleted, dummy, parentIsDeleted FROM Folder");
            database.execSQL("DROP TABLE Folder");
            database.execSQL("ALTER TABLE Folder_new RENAME TO Folder");

            database.execSQL("CREATE TABLE Size_new(" +
                    "id INTEGER NOT NULL PRIMARY KEY," +
                    "orderNumber INTEGER NOT NULL," +
                    "createdTime TEXT," +
                    "modifiedTime TEXT," +
                    "imageUri TEXT," +
                    "brand TEXT," +
                    "name TEXT," +
                    "size TEXT," +
                    "link TEXT," +
                    "memo TEXT," +
                    "parentCategory INTEGER NOT NULL DEFAULT 0," +
                    "parentId INTEGER NOT NULL," +
                    "isFavorite INTEGER NOT NULL," +
                    "isDeleted INTEGER NOT NULL," +
                    "isParentDeleted INTEGER NOT NULL," +
                    "sizeMap TEXT)");
            database.execSQL("INSERT INTO Size_new(id, orderNumber, createdTime, " +
                    "modifiedTime, imageUri, brand, name, size, link, memo, parentId, isFavorite, isDeleted, isParentDeleted, sizeMap)" +
                    "SELECT id, orderNumber, createdTime, modifiedTime, imageUri, brand, name, size, link, memo, parentId," +
                    "isFavorite, isDeleted, parentIsDeleted, sizeMap FROM Size");
            database.execSQL("DROP TABLE Size");
            database.execSQL("ALTER TABLE Size_new RENAME TO Size");

            database.execSQL("CREATE TABLE RecentSearch_new(" +
                    "id INTEGER NOT NULL PRIMARY KEY," +
                    "word TEXT," +
                    "date TEXT," +
                    "type INTEGER NOT NULL," +
                    "parentCategory INTEGER NOT NULL DEFAULT 0," +
                    "parentId INTEGER NOT NULL)");
            database.execSQL("INSERT INTO RecentSearch_new(id, word, date, type, parentId)" +
                    "SELECT id, word, date, type, parentId FROM RecentSearch");
            database.execSQL("DROP TABLE RecentSearch");
            database.execSQL("ALTER TABLE RecentSearch_new RENAME TO RecentSearch");
        }
    };
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE RecentSearch ADD COLUMN parentId INTEGER NOT NULL DEFAULT -1");
            database.execSQL("ALTER TABLE Category ADD COLUMN parentId INTEGER NOT NULL DEFAULT -1");
        }
    };
    private static AppDataBase sInstance;

    public synchronized static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDataBase.class, "db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            List<Category> categoryList = new ArrayList<>();
                            long id = CommonUtil.createId();
                            //TODO test
                            categoryList.add(new Category(id++, "반팔", Constant.ParentCategory.TOP.ordinal(), 1));
                            categoryList.add(new Category(id++, "긴팔", Constant.ParentCategory.TOP.ordinal(), 2));
                            categoryList.add(new Category(id++, "니트", Constant.ParentCategory.TOP.ordinal(), 3));
                            categoryList.add(new Category(id++, "후드", Constant.ParentCategory.TOP.ordinal(), 4));
                            categoryList.add(new Category(id++, "셔츠", Constant.ParentCategory.TOP.ordinal(), 5));
                            categoryList.add(new Category(id++, "청바지", Constant.ParentCategory.BOTTOM.ordinal(), 6));
                            categoryList.add(new Category(id++, "슬랙스", Constant.ParentCategory.BOTTOM.ordinal(), 7));
                            categoryList.add(new Category(id++, "반바지", Constant.ParentCategory.BOTTOM.ordinal(), 8));
                            categoryList.add(new Category(id++, "트랙 팬츠", Constant.ParentCategory.BOTTOM.ordinal(), 9));
                            categoryList.add(new Category(id++, "Ma-1", Constant.ParentCategory.OUTER.ordinal(), 10));
                            categoryList.add(new Category(id++, "패딩", Constant.ParentCategory.OUTER.ordinal(), 11));
                            categoryList.add(new Category(id++, "야상", Constant.ParentCategory.OUTER.ordinal(), 12));
                            categoryList.add(new Category(id++, "후드 집업", Constant.ParentCategory.OUTER.ordinal(), 13));
                            categoryList.add(new Category(id++, "신발", Constant.ParentCategory.ETC.ordinal(), 14));
                            categoryList.add(new Category(id++, "안경", Constant.ParentCategory.ETC.ordinal(), 15));
                            categoryList.add(new Category(id++, "목걸이", Constant.ParentCategory.ETC.ordinal(), 16));
                            categoryList.add(new Category(id, "기타", Constant.ParentCategory.ETC.ordinal(), 17));
                            new Repository(context).getCategoryRepository().insertCategory(categoryList);
                        }
                    }).addMigrations(MIGRATION_2_1)
                    .build();
        }
        return sInstance;
    }

    public abstract CategoryDao categoryDao();

    public abstract SizeDao sizeDao();

    public abstract FolderDao folderDao();

    public abstract RecentSearchDao recentSearchDao();
}
