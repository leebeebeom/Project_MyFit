package com.example.project_myfit.ui.main.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.project_myfit.MyFitConstant;
import com.example.project_myfit.ui.main.listfragment.database.Converters;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.FolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Category.class, Size.class, Folder.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase sInstance;

    public synchronized static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDataBase.class, "db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            List<Category> categoryList = new ArrayList<>();
                            categoryList.add(new Category("반팔", MyFitConstant.TOP, 1));
                            categoryList.add(new Category("긴팔", MyFitConstant.TOP, 2));
                            categoryList.add(new Category("니트", MyFitConstant.TOP, 3));
                            categoryList.add(new Category("후드", MyFitConstant.TOP, 4));
                            categoryList.add(new Category("셔츠", MyFitConstant.TOP, 5));
                            categoryList.add(new Category("청바지", MyFitConstant.BOTTOM, 6));
                            categoryList.add(new Category("슬랙스", MyFitConstant.BOTTOM, 7));
                            categoryList.add(new Category("반바지", MyFitConstant.BOTTOM, 8));
                            categoryList.add(new Category("트랙 팬츠", MyFitConstant.BOTTOM, 9));
                            categoryList.add(new Category("Ma-1", MyFitConstant.OUTER, 10));
                            categoryList.add(new Category("패딩", MyFitConstant.OUTER, 11));
                            categoryList.add(new Category("야상", MyFitConstant.OUTER, 12));
                            categoryList.add(new Category("후드 집업", MyFitConstant.OUTER, 13));
                            categoryList.add(new Category("신발", MyFitConstant.ETC, 14));
                            categoryList.add(new Category("안경", MyFitConstant.ETC, 15));
                            categoryList.add(new Category("목걸이", MyFitConstant.ETC, 16));
                            categoryList.add(new Category("기타", MyFitConstant.ETC, 17));
                            //insert
                            new Thread(() -> {
                                for (Category category : categoryList) {
                                    getsInstance(context).categoryDao().insert(category);
                                }
                            }).start();
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }

    public abstract CategoryDao categoryDao();

    public abstract SizeDao sizeDao();

    public abstract FolderDao folderDao();
}
