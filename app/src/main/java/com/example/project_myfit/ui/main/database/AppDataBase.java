package com.example.project_myfit.ui.main.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.ListFolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Category.class, Size.class, ListFolder.class}, version = 1, exportSchema = false)
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
                            categoryList.add(new Category("Short Sleeve", "TOP", 1));
                            categoryList.add(new Category("Long Sleeve", "TOP", 2));
                            categoryList.add(new Category("Knit", "TOP", 3));
                            categoryList.add(new Category("Hood", "TOP", 4));
                            categoryList.add(new Category("Shirt", "TOP", 5));
                            categoryList.add(new Category("Jean", "BOTTOM", 1));
                            categoryList.add(new Category("Slacks", "BOTTOM", 2));
                            categoryList.add(new Category("Short Pant", "BOTTOM", 3));
                            categoryList.add(new Category("Track Pant", "BOTTOM", 4));
                            categoryList.add(new Category("Ma-1", "OUTER", 1));
                            categoryList.add(new Category("Shall Parka", "OUTER", 2));
                            categoryList.add(new Category("M65", "OUTER", 3));
                            categoryList.add(new Category("Hood Zip-Up", "OUTER", 4));
                            categoryList.add(new Category("Shoes", "ETC", 1));
                            categoryList.add(new Category("Glass", "ETC", 2));
                            categoryList.add(new Category("Necklace", "ETC", 3));
                            categoryList.add(new Category("Ring", "ETC", 4));
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

    public abstract ListFolderDao listFolderDao();
}
