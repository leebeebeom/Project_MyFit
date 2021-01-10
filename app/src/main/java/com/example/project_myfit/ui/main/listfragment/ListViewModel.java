package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.FolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ListViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private final FolderDao mFolderDao;
    private int mFolderLargestOrder;
    private String mActionBarTitle;

    public ListViewModel(@NonNull Application application) {
        super(application);
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        mFolderDao = AppDataBase.getsInstance(application).listFolderDao();
    }

    public long getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(date));
    }

    //folderDao-------------------------------------------------------------------------------------
    public LiveData<List<Folder>> getAllFolder(int folderId) {
        return mFolderDao.getAllFolder(folderId);
    }

    public void insertFolder(Folder folder) {
        new Thread(() -> mFolderDao.insert(folder)).start();
    }

    public void updateFolderOrder(List<Folder> folderList) {
        new Thread(() -> mFolderDao.updateOrder(folderList)).start();
    }

    public void deleteFolder(List<Folder> folderList) {
        new Thread(() -> mFolderDao.delete(folderList)).start();
    }
    //----------------------------------------------------------------------------------------------

    //sizeDao---------------------------------------------------------------------------------------
    public LiveData<List<Size>> getAllSize(int folderId) {
        return mSizeDao.getAllSize(folderId);
    }

    public void deleteSize(List<Size> sizeList) {
        for (Size s : sizeList) {
            if (s.getImageUri() != null) {
                File file = new File(Uri.parse(s.getImageUri()).getPath());
                if (file.delete()) {
                    Log.d(TAG, "deleteSize: file delete success");
                } else {
                    Log.d(TAG, "deleteSize: file delete fail");
                }
            }
        }
        new Thread(() -> mSizeDao.delete(sizeList)).start();
    }

    public void updateSizeOrder(List<Size> sizeList) {
        new Thread(() -> mSizeDao.updateOrder(sizeList)).start();
    }
    //----------------------------------------------------------------------------------------------

    //getter & setter-------------------------------------------------------------------------------
    public void setActionBarTitle(String actionBarTitle) {
        mActionBarTitle = actionBarTitle;
    }

    public String getActionBarTitle() {
        return mActionBarTitle;
    }

    public void setFolderLargestOrder() {
        new Thread(() -> mFolderLargestOrder = mFolderDao.getLargestOrder()).start();
    }

    public int getFolderLargestOrder() {
        return mFolderLargestOrder;
    }
    //----------------------------------------------------------------------------------------------

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
        Glide.with(imageView.getContext()).load("").error(R.drawable.icon_drag_handle).into(imageView);
    }

    @BindingAdapter("setAddIcon")
    public static void setAddIcon(ImageView imageView, String dummy) {
        Glide.with(imageView.getContext()).load("").error(R.drawable.icon_add_image).into(imageView);
    }
    //----------------------------------------------------------------------------------------------
}