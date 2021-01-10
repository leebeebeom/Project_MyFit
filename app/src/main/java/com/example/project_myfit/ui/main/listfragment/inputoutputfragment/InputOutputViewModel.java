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

public class InputOutputViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private int mLargestOrder;
    private String mFileName;
    private File mCacheFile;
    private boolean isOutput;
    private Size mSize, mNewSize, mOldSize;
    private Uri mCacheFileUri, mSavedFileUri, mOriginFileUri;

    public InputOutputViewModel(@NonNull Application application) {
        super(application);
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
    }

    public Intent getCropIntent(Uri data) {
        //get current time
        String pattern = "yyyyMMddHHmmss";
        String currentDateTime = getCurrentTime(pattern);

        //file name
        mFileName = "MyFit_" + currentDateTime + ".jpg";

        //make file
        mCacheFile = new File(getApplication().getExternalCacheDir(), mFileName);

        //intent
        Uri uri = Uri.fromFile(mCacheFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*")
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    public void inputFabClick() {
        //if there's a added image
        if (mCacheFileUri != null) {
            //file move cache folder to picture folder
            File pictureFolderPath = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
            mSavedFileUri = Uri.fromFile(pictureFolderPath);
            if (mCacheFile.renameTo(pictureFolderPath))
                Log.d(TAG, "inputFabClick: file rename success");
            else Log.d(TAG, "inputFabClick: file rename fail");
        }
    }

    public void outputFabClick() {
        //there was a saved image, but user deleted it
        if (mSize.getImageUri() != null && mOriginFileUri == null && mCacheFileUri == null) {
            //delete origin image file
            File OriginImageFile = new File(Uri.parse(mSize.getImageUri()).getPath());
            if (OriginImageFile.delete()) Log.d(TAG, "outputFabClick: file delete success");
            else Log.d(TAG, "outputFabClick: file delete fail");
        } else if (mCacheFileUri != null) {
            //user replaces with a new image
            File pictureFolderPath = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
            mSavedFileUri = Uri.fromFile(pictureFolderPath);
            if (mCacheFile.renameTo(pictureFolderPath))
                Log.d(TAG, "outputFabClick: file rename success");
            else Log.d(TAG, "outputFabClick: file rename fail");

            //delete origin image file
            if (mSize.getImageUri() != null) {
                File originFile = new File(Uri.parse(mSize.getImageUri()).getPath());
                if (originFile.delete()) Log.d(TAG, "outputFabClick: file delete success");
                else Log.d(TAG, "outputFabClick: file delete fail");
            }
        }
    }

    public String getCurrentTime(String pattern) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    //dao-------------------------------------------------------------------------------------------
    public void insert(Size size) {
        new Thread(() -> mSizeDao.insert(size)).start();
    }

    public void update(Size size) {
        new Thread(() -> mSizeDao.update(size)).start();
    }

    public void delete(Size size) {
        new Thread(() -> mSizeDao.delete(size)).start();
    }
    //----------------------------------------------------------------------------------------------


    //getter & setter-------------------------------------------------------------------------------
    public void setLargestOrder() {
        new Thread(() -> mLargestOrder = mSizeDao.getLargestOrder()).start();
    }

    public int getLargestOrder() {
        return mLargestOrder;
    }

    public void setIsOutput(boolean output) {
        isOutput = output;
    }

    public boolean isOutput() {
        return isOutput;
    }

    //set originFileUri
    public void setSize(Size mSize) {
        this.mSize = mSize;
        mOriginFileUri = mSize.getImageUri() != null ? Uri.parse(mSize.getImageUri()) : null;
    }

    public Size getSize() {
        return mSize;
    }

    public void setNewSize(Size mNewSize) {
        this.mNewSize = mNewSize;
    }

    public Size getNewSize() {
        return mNewSize;
    }

    public Size getOldSize() {
        return mOldSize;
    }

    public void setOldSize(int id) {
        new Thread(() -> mOldSize = mSizeDao.getSizeById(id)).start();
    }

    public void setOriginFileUri(Uri mOriginFileUri) {
        this.mOriginFileUri = mOriginFileUri;
    }

    public Uri getOriginFileUri() {
        return mOriginFileUri;
    }

    public void setCacheFileUri(Uri mCacheFileUri) {
        this.mCacheFileUri = mCacheFileUri;
    }

    public Uri getCacheFileUri() {
        return mCacheFileUri;
    }

    public Uri getSavedFileUri() {
        return mSavedFileUri;
    }
    //----------------------------------------------------------------------------------------------

    //bindingAdapter--------------------------------------------------------------------------------
    @BindingAdapter("setUriOutput")
    public static void setUriOutput(ImageView imageView, String uriString) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            imageView.setImageURI(uri);
        }
    }

    @BindingAdapter("android:text")
    public static void setSelection(TextInputEditText editText, String text) {
        if (text != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }
    //----------------------------------------------------------------------------------------------
}