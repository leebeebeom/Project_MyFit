package com.example.myfit.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.RecentSearch;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.Size;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.data.repository.dao.RecentSearchDao;
import com.example.myfit.data.repository.dao.SizeDao;
import com.example.myfit.util.constant.ParentCategory;

import org.jetbrains.annotations.NotNull;

@Database(entities = {Category.class, Size.class, Folder.class, RecentSearch.class}, version = 2, exportSchema = false)
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
            database.execSQL("ALTER TABLE Category RENAME COLUMN parentCategory TO parentCategoryIndex ");
            database.execSQL("ALTER TABLE Folder RENAME COLUMN parentCategory TO parentCategoryIndex ");
            database.execSQL("ALTER TABLE Size RENAME COLUMN parentCategory TO parentCategoryIndex ");
            database.execSQL("ALTER TABLE RecentSearch RENAME COLUMN parentCategory TO parentCategoryIndex ");
        }
    };
    private static AppDataBase sInstance;

    public synchronized static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDataBase.class, "myFit.db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Category[] categories = new Category[17];
                            //TODO test
                            categories[0] = ModelFactory.makeCategory("반팔", ParentCategory.TOP.getIndex(), 1);
                            categories[1] = ModelFactory.makeCategory("긴팔", ParentCategory.TOP.getIndex(), 2);
                            categories[2] = ModelFactory.makeCategory("니트", ParentCategory.TOP.getIndex(), 3);
                            categories[3] = ModelFactory.makeCategory("후드", ParentCategory.TOP.getIndex(), 4);
                            categories[4] = ModelFactory.makeCategory("셔츠", ParentCategory.TOP.getIndex(), 5);

                            categories[5] = ModelFactory.makeCategory("청바지", ParentCategory.BOTTOM.getIndex(), 6);
                            categories[6] = ModelFactory.makeCategory("슬랙스", ParentCategory.BOTTOM.getIndex(), 7);
                            categories[7] = ModelFactory.makeCategory("반바지", ParentCategory.BOTTOM.getIndex(), 8);

                            categories[8] = ModelFactory.makeCategory("Ma-1", ParentCategory.OUTER.getIndex(), 9);
                            categories[9] = ModelFactory.makeCategory("패딩", ParentCategory.OUTER.getIndex(), 10);
                            categories[10] = ModelFactory.makeCategory("야상", ParentCategory.OUTER.getIndex(), 11);
                            categories[11] = ModelFactory.makeCategory("후드 집업", ParentCategory.OUTER.getIndex(), 12);

                            categories[12] = ModelFactory.makeCategory("신발", ParentCategory.ETC.getIndex(), 13);
                            categories[13] = ModelFactory.makeCategory("안경", ParentCategory.ETC.getIndex(), 14);
                            categories[14] = ModelFactory.makeCategory("목걸이", ParentCategory.ETC.getIndex(), 15);
                            categories[15] = ModelFactory.makeCategory("기타", ParentCategory.ETC.getIndex(), 16);

                            new CategoryRepository(context).insert(categories);
                        }
                    })
                    .build();
        }
        return sInstance;
    }

    public abstract CategoryDao categoryDao();

    public abstract SizeDao sizeDao();

    public abstract FolderDao folderDao();

    public abstract RecentSearchDao recentSearchDao();
}
