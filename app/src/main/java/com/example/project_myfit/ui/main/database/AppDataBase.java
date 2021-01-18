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
                            categoryList.add(new Category("Short Sleeve", MyFitConstant.TOP, 1));
                            categoryList.add(new Category("Long Sleeve", MyFitConstant.TOP, 2));
                            categoryList.add(new Category("Knit", MyFitConstant.TOP, 3));
                            categoryList.add(new Category("Hood", MyFitConstant.TOP, 4));
                            categoryList.add(new Category("Shirt", MyFitConstant.TOP, 5));
                            categoryList.add(new Category("Jean", MyFitConstant.BOTTOM, 6));
                            categoryList.add(new Category("Slacks", MyFitConstant.BOTTOM, 7));
                            categoryList.add(new Category("Short Pant", MyFitConstant.BOTTOM, 8));
                            categoryList.add(new Category("Track Pant", MyFitConstant.BOTTOM, 9));
                            categoryList.add(new Category("Ma-1", MyFitConstant.OUTER, 10));
                            categoryList.add(new Category("Shall Parka", MyFitConstant.OUTER, 11));
                            categoryList.add(new Category("M65", MyFitConstant.OUTER, 12));
                            categoryList.add(new Category("Hood Zip-Up", MyFitConstant.OUTER, 13));
                            categoryList.add(new Category("Shoes", MyFitConstant.ETC, 14));
                            categoryList.add(new Category("Glass", MyFitConstant.ETC, 15));
                            categoryList.add(new Category("Necklace", MyFitConstant.ETC, 16));
                            categoryList.add(new Category("Ring", MyFitConstant.ETC, 17));
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
