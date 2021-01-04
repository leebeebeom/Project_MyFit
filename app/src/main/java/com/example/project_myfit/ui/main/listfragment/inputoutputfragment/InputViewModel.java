package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class InputViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private int mLargestOrder;
    private String mFileName;
    private File mCacheFile;
    private boolean isOutput;
    private Size mSize, mNewSize, mOldSize;
    private Uri mCacheFileUri, mSavedFileUri, mOriginFileUri;

    public InputViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
    }

    //Set Uri
    @BindingAdapter("setUri")
    public static void setUri(ImageView imageView, String uriString) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            imageView.setImageURI(uri);
        }
    }

    //Set setSelection
    @BindingAdapter("android:text")
    public static void setCursor(TextInputEditText editText, String text) {
        if (text != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }

    }

    //Get Crop Intent
    public Intent getCropIntent(Uri data) {
        //Current Time
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentDateTime = dateFormat.format(date);
        //File Name
        mFileName = "MyFit_" + currentDateTime + ".jpg";
        //Make File
        mCacheFile = new File(getApplication().getExternalCacheDir(), mFileName);
        //Intent
        Uri uri = Uri.fromFile(mCacheFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*")
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    //Input Fab Click
    public void inputFabClick() {
        //If there's a added image
        if (mCacheFileUri != null) {
            //Cache folder to picture folder
            File toPath = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
            mSavedFileUri = Uri.fromFile(toPath);
            if (mCacheFile.renameTo(toPath)) Log.d(TAG, "setClickListener: 파일 이동됨");
            else Log.d(TAG, "setClickListener: 파일 이동 실패");
        }
    }

    //Output Fab Click
    public void outputFabClick() {
        //There was a saved image, but the user deleted it
        if (mSize.getImageUri() != null && mOriginFileUri == null && mCacheFileUri == null) {

            //Delete origin image file
            File OriginImageFile = new File(Uri.parse(mSize.getImageUri()).getPath());
            if (OriginImageFile.delete()) Log.d(TAG, "outputFabClick: 파일 삭제됨");
            else Log.d(TAG, "outputFabClick: 파일 삭제 실패");
        } else if (mCacheFileUri != null) {

            //user replaces with a new image
            File toPath = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
            mSavedFileUri = Uri.fromFile(toPath);
            if (mCacheFile.renameTo(toPath)) Log.d(TAG, "outputFabClick: 파일 이동됨");
            else Log.d(TAG, "outputFabClick: 파일 이동 실패");

            //Delete origin image file
            if (mSize.getImageUri() != null) {
                File deleteFile = new File(Uri.parse(mSize.getImageUri()).getPath());
                if (deleteFile.delete()) Log.d(TAG, "outputFabClick: 파일 삭제됨");
                else Log.d(TAG, "outputFabClick: 파일 삭제 실패");
            }

        }
    }

    //Get Current Time
    public String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat(" yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    //Insert
    public void insert(Size size) {
        new Thread(() -> mSizeDao.insert(size)).start();
    }

    //Update
    public void update(Size size) {
        new Thread(() -> mSizeDao.update(size)).start();
    }

    //Set LargestOrder
    public void setLargestOrder() {
        new Thread(() -> mLargestOrder = mSizeDao.getLargestOrder()).start();
    }

    //Get LargestOrder
    public int getLargestOrder() {
        return mLargestOrder;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public void setIsOutput(boolean output) {
        isOutput = output;
    }

    public Size getSize() {
        return mSize;
    }

    //Set mOriginFileUri
    public void setSize(Size mSize) {
        this.mSize = mSize;
        mOriginFileUri = mSize.getImageUri() != null ? Uri.parse(mSize.getImageUri()) : null;
    }

    public Uri getCacheFileUri() {
        return mCacheFileUri;
    }

    public void setCacheFileUri(Uri mCacheFileUri) {
        this.mCacheFileUri = mCacheFileUri;
    }

    public Uri getSavedFileUri() {
        return mSavedFileUri;
    }

    public Size getNewSize() {
        return mNewSize;
    }

    public void setNewSize(Size mNewSize) {
        this.mNewSize = mNewSize;
    }

    public Uri getOriginFileUri() {
        return mOriginFileUri;
    }

    public void setOriginFileUri(Uri mOriginFileUri) {
        this.mOriginFileUri = mOriginFileUri;
    }

    public Size getOldSize() {
        return mOldSize;
    }

    public void setOldSize(int id) {
        new Thread(() -> mOldSize = mSizeDao.getSizeById(id)).start();
    }
}