package com.example.myfit.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.model.model.RecentSearch;
import com.example.myfit.data.model.model.Size;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.dao.AutoCompleteDao;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.data.repository.dao.RecentSearchDao;
import com.example.myfit.data.repository.dao.SizeDao;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.constant.ParentCategory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Database(entities = {Category.class, Size.class, Folder.class, RecentSearch.class}, version = 2, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase sInstance;

    public synchronized static AppDataBase getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context, AppDataBase.class, "myFit.db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            List<Category> categories = new LinkedList<>();
                            categories.add(ModelFactory.makeCategory("반팔", ParentCategory.TOP.getValue(), 1));
                            categories.add(ModelFactory.makeCategory("긴팔", ParentCategory.TOP.getValue(), 2));
                            categories.add(ModelFactory.makeCategory("니트", ParentCategory.TOP.getValue(), 3));
                            categories.add(ModelFactory.makeCategory("후드", ParentCategory.TOP.getValue(), 4));
                            categories.add(ModelFactory.makeCategory("셔츠", ParentCategory.TOP.getValue(), 5));

                            categories.add(ModelFactory.makeCategory("청바지", ParentCategory.BOTTOM.getValue(), 6));
                            categories.add(ModelFactory.makeCategory("슬랙스", ParentCategory.BOTTOM.getValue(), 7));
                            categories.add(ModelFactory.makeCategory("반바지", ParentCategory.BOTTOM.getValue(), 8));

                            categories.add(ModelFactory.makeCategory("Ma-1", ParentCategory.OUTER.getValue(), 9));
                            categories.add(ModelFactory.makeCategory("패딩", ParentCategory.OUTER.getValue(), 10));
                            categories.add(ModelFactory.makeCategory("야상", ParentCategory.OUTER.getValue(), 11));
                            categories.add(ModelFactory.makeCategory("후드 집업", ParentCategory.OUTER.getValue(), 12));

                            categories.add(ModelFactory.makeCategory("신발", ParentCategory.ETC.getValue(), 13));
                            categories.add(ModelFactory.makeCategory("안경", ParentCategory.ETC.getValue(), 14));
                            categories.add(ModelFactory.makeCategory("목걸이", ParentCategory.ETC.getValue(), 15));
                            categories.add(ModelFactory.makeCategory("기타", ParentCategory.ETC.getValue(), 16));


                            AtomicLong id = new AtomicLong(CommonUtil.getCurrentDate());
                            categories.forEach(category -> category.setId(id.incrementAndGet()));

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

    public abstract AutoCompleteDao autoCompleteDao();
}
