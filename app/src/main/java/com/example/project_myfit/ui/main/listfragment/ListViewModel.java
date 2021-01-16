package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.database.CategoryDao;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.FolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private final FolderDao mFolderDao;
    private final CategoryDao mCategoryDao;
    private List<Category> mAllCategoryList;
    private List<Folder> mAllFolderList;
    private int mFolderLargestOrder;
    private Folder mThisFolder;

    public ListViewModel(@NonNull Application application) {
        super(application);
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        mFolderDao = AppDataBase.getsInstance(application).listFolderDao();
        mCategoryDao = AppDataBase.getsInstance(application).categoryDao();
    }

    public long getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(date));
    }

    //folderDao-------------------------------------------------------------------------------------
    public LiveData<List<Folder>> getFolderListLive(long folderId) {
        return mFolderDao.getFolderListLive(folderId, false);
    }

    public List<Folder> getAllFolder() {
        Thread thread = new Thread(() -> mAllFolderList = mFolderDao.getAllFolder());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mAllFolderList;
    }

    public int getFolderLargestOrder() {
        Thread thread = new Thread(() -> mFolderLargestOrder = mFolderDao.getLargestOrder());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mFolderLargestOrder;
    }

    public void insertFolder(Folder folder) {
        new Thread(() -> mFolderDao.insert(folder)).start();
    }

    public void updateFolder(Folder folder) {
        new Thread(() -> mFolderDao.update(folder)).start();
    }

    public void updateFolderList(List<Folder> folderList) {
        new Thread(() -> mFolderDao.updateOrder(folderList)).start();
    }

    public void deleteFolder(List<Folder> folderList) {
        for (Folder f : folderList) f.setDeleted(true);
        new Thread(() -> mFolderDao.delete(folderList)).start();
    }
    //----------------------------------------------------------------------------------------------

    //sizeDao---------------------------------------------------------------------------------------
    public LiveData<List<Size>> getAllSize(long folderId) {
        return mSizeDao.getAllSizeLive(folderId, false);
    }

    public void deleteSize(List<Size> sizeList) {
        for (Size s : sizeList) s.setDeleted(true);
        new Thread(() -> mSizeDao.update(sizeList)).start();
    }

    public void updateSizeList(List<Size> sizeList) {
        new Thread(() -> mSizeDao.updateOrder(sizeList)).start();
    }
    //----------------------------------------------------------------------------------------------

    //categoryDao-----------------------------------------------------------------------------------
    public List<Category> getAllCategoryList(String parentCategory) {
        Thread thread = new Thread(() -> mAllCategoryList = mCategoryDao.getAllCategory2(parentCategory));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mAllCategoryList;
    }
    //----------------------------------------------------------------------------------------------


    public Folder getThisFolder() {
        return mThisFolder;
    }

    public void setThisFolder(Folder mThisFolder) {
        this.mThisFolder = mThisFolder;
    }

    //bindingAdapter--------------------------------------------------------------------------------
    @BindingAdapter("setUri")
    public static void setUri(ImageView imageView, String uriString) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            Glide.with(imageView.getContext()).load(uri).into(imageView);
        }
    }

    @BindingAdapter("setDragHandle")
    public static void setDragHandle(ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).fallback(R.drawable.icon_drag_handle).into(imageView);
    }

    @BindingAdapter("setAddIcon")
    public static void setAddIcon(ImageView imageView, String dummy) {
        dummy = null;
        Glide.with(imageView.getContext()).load(dummy).fallback(R.drawable.icon_add_image).into(imageView);
    }
    //----------------------------------------------------------------------------------------------
}