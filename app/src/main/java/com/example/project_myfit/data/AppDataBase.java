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
import com.example.project_myfit.util.MyFitConstant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Category.class, Size.class, Folder.class, RecentSearch.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {
    private static final Migration MIGRATION_2_1 = new Migration(2, 1) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE RecentSearch_new(" +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "word TEXT," +
                    "date TEXT," +
                    "type INTERGER NOT NULL DEFAULT 0," +
                    "parentCategory TEXT DEFAULT NULL)");
            database.execSQL("INSERT INTO RecentSearch_new(id, word, date, type)" +
                    "SELECT id, word, date, type FROM RecentSearch");
            database.execSQL("DROP TABLE RecentSearch");
            database.execSQL("ALTER TABLE RecentSearch_new RENAME TO RecentSearch");
        }
    };
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull @NotNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE RecentSearch_new(" +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "word TEXT," +
                    "date TEXT," +
                    "isRecycleBin INTERGER NOT NULL DEFAULT 0)");
            database.execSQL("INSERT INTO RecentSearch_new(id, word, date)" +
                    "SELECT id, word, date FROM RecentSearch");
            database.execSQL("DROP TABLE RecentSearch");
            database.execSQL("ALTER TABLE RecentSearch_new RENAME TO RecentSearch");
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
                            categoryList.add(new Category(1, "반팔", MyFitConstant.TOP, 1));
                            categoryList.add(new Category(2, "긴팔", MyFitConstant.TOP, 2));
                            categoryList.add(new Category(3, "니트", MyFitConstant.TOP, 3));
                            categoryList.add(new Category(4, "후드", MyFitConstant.TOP, 4));
                            categoryList.add(new Category(5, "셔츠", MyFitConstant.TOP, 5));
                            categoryList.add(new Category(6, "청바지", MyFitConstant.BOTTOM, 6));
                            categoryList.add(new Category(7, "슬랙스", MyFitConstant.BOTTOM, 7));
                            categoryList.add(new Category(8, "반바지", MyFitConstant.BOTTOM, 8));
                            categoryList.add(new Category(9, "트랙 팬츠", MyFitConstant.BOTTOM, 9));
                            categoryList.add(new Category(10, "Ma-1", MyFitConstant.OUTER, 10));
                            categoryList.add(new Category(11, "패딩", MyFitConstant.OUTER, 11));
                            categoryList.add(new Category(12, "야상", MyFitConstant.OUTER, 12));
                            categoryList.add(new Category(13, "후드 집업", MyFitConstant.OUTER, 13));
                            categoryList.add(new Category(14, "신발", MyFitConstant.ETC, 14));
                            categoryList.add(new Category(15, "안경", MyFitConstant.ETC, 15));
                            categoryList.add(new Category(16, "목걸이", MyFitConstant.ETC, 16));
                            categoryList.add(new Category(17, "기타", MyFitConstant.ETC, 17));
                            Repository.getCategoryRepository(context).categoryInsert(categoryList);
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
