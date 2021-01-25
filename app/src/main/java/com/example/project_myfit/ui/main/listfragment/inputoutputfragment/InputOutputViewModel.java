package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.BuildConfig;
import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.example.project_myfit.MyFitConstant.BOTTOM_LENGTH;
import static com.example.project_myfit.MyFitConstant.CHEST;
import static com.example.project_myfit.MyFitConstant.HEM;
import static com.example.project_myfit.MyFitConstant.LENGTH;
import static com.example.project_myfit.MyFitConstant.OPTION1;
import static com.example.project_myfit.MyFitConstant.OPTION2;
import static com.example.project_myfit.MyFitConstant.OPTION3;
import static com.example.project_myfit.MyFitConstant.OPTION4;
import static com.example.project_myfit.MyFitConstant.OPTION5;
import static com.example.project_myfit.MyFitConstant.OPTION6;
import static com.example.project_myfit.MyFitConstant.RISE;
import static com.example.project_myfit.MyFitConstant.SHOULDER;
import static com.example.project_myfit.MyFitConstant.SLEEVE;
import static com.example.project_myfit.MyFitConstant.THIGH;
import static com.example.project_myfit.MyFitConstant.WAIST;

public class InputOutputViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private Size mCompareSize, mNewSize;
    private MutableLiveData<Uri> mImageUri;
    private File mCacheFile;
    private String mFileName;
    private MainActivityViewModel mActivityModel;
    private long mFolderId;

    public InputOutputViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    //dao-------------------------------------------------------------------------------------------
    public void sizeInsert() {
        mRepository.sizeInsert(getNewSize());
        increaseItemAmount();
    }

    public void update() {
        mRepository.sizeUpdate(getUpdatedSize());
    }

    public void delete() {
        mActivityModel.getSize().setDeleted(true);
        mRepository.sizeUpdate(mActivityModel.getSize());
        decreaseItemAmount();
    }
    //----------------------------------------------------------------------------------------------

    public void checkInputOutput(MainActivityViewModel activityModel) {
        if (mActivityModel == null) mActivityModel = activityModel;
        if (mFolderId == 0)
            mFolderId = mActivityModel.getFolder() == null ? mActivityModel.getCategory().getId() : mActivityModel.getFolder().getId();
        if (mActivityModel.getSize() != null) {
            if (mCompareSize == null)
                mCompareSize = mRepository.getSize(mActivityModel.getSize().getId());
            if (mImageUri == null) {
                mImageUri = new MutableLiveData<>();
                mImageUri.setValue(mActivityModel.getSize().getImageUri() != null ? Uri.parse(mActivityModel.getSize().getImageUri()) : null);
            }
        }
    }

    public boolean getCompareResult() {
        return !String.valueOf(mCompareSize.getImageUri()).equals(String.valueOf(mImageUri.getValue())) ||
                !mActivityModel.getSize().isFavorite() == mCompareSize.isFavorite() ||
                !String.valueOf(mActivityModel.getSize().getBrand()).equals(String.valueOf(mCompareSize.getBrand())) ||
                !String.valueOf(mActivityModel.getSize().getName()).equals(String.valueOf(mCompareSize.getName())) ||
                !String.valueOf(mActivityModel.getSize().getSize()).equals(String.valueOf(mCompareSize.getSize())) ||
                !String.valueOf(mActivityModel.getSize().getLink()).equals(String.valueOf(mCompareSize.getLink())) ||
                !String.valueOf(mActivityModel.getSize().getMemo()).equals(String.valueOf(mCompareSize.getMemo())) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(LENGTH)).equals(String.valueOf(mCompareSize.getSizeMap().get(LENGTH))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(SHOULDER)).equals(String.valueOf(mCompareSize.getSizeMap().get(SHOULDER))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(CHEST)).equals(String.valueOf(mCompareSize.getSizeMap().get(CHEST))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(SLEEVE)).equals(String.valueOf(mCompareSize.getSizeMap().get(SLEEVE))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(BOTTOM_LENGTH)).equals(String.valueOf(mCompareSize.getSizeMap().get(BOTTOM_LENGTH))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(WAIST)).equals(String.valueOf(mCompareSize.getSizeMap().get(WAIST))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(THIGH)).equals(String.valueOf(mCompareSize.getSizeMap().get(THIGH))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(RISE)).equals(String.valueOf(mCompareSize.getSizeMap().get(RISE))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(HEM)).equals(String.valueOf(mCompareSize.getSizeMap().get(HEM))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(OPTION1)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION1))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(OPTION2)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION2))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(OPTION3)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION3))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(OPTION4)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION4))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(OPTION5)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION5))) ||
                !String.valueOf(mActivityModel.getSize().getSizeMap().get(OPTION6)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION6)));
    }

    public Size getSize() {
        if (mActivityModel.getSize() == null) {
            if (mNewSize == null) mNewSize = new Size();
            return mNewSize;
        } else return mActivityModel.getSize();
    }

    public MutableLiveData<Uri> getImageUri() {
        if (mImageUri == null) mImageUri = new MutableLiveData<>();
        return mImageUri;
    }

    public Intent getCropIntent(Uri data) {
        //file name
        mFileName = "MyFit_" + getCurrentTime("yyyyMMddHHmmss") + ".jpg";

        //make file
        mCacheFile = new File(getApplication().getExternalCacheDir(), mFileName);

        //intent
        //Uri uri = Uri.fromFile(mCacheFile); <- No
        Uri uri = FileProvider.getUriForFile(getApplication().getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", mCacheFile);

        Intent intent = new Intent("com.android.camera.action.CROP")
                .setDataAndType(data, "image/*")
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);

        //grant
        List<ResolveInfo> resolveInfoList = getApplication().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getApplication().grantUriPermission(packageName, uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        return intent;
    }

    private Size getNewSize() {
        mNewSize.setOrderNumber(mRepository.getSizeLargestOrder() + 1);
        mNewSize.setCreatedTime(getCurrentTime(" yyyy년 MM월 dd일 HH:mm:ss"));
        mNewSize.setModifiedTime("");
        mNewSize.setImageUri(mImageUri.getValue() != null ? getRenameUri() : null);
        mNewSize.setFolderId(mFolderId);
        return mNewSize;
    }

    private Size getUpdatedSize() {
        mActivityModel.getSize().setImageUri(getOutputFileSavedUri());
        mActivityModel.getSize().setModifiedTime(getCurrentTime(" yyyy년 MM월 dd일 HH:mm:ss"));
        return mActivityModel.getSize();
    }

    private String getCurrentTime(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    private String getOutputFileSavedUri() {
        //there was a saved image, but user deleted it
        if (mActivityModel.getSize().getImageUri() != null && mImageUri.getValue() == null) {
            //delete origin image file
            originFileDelete();
            return null;
        } else if (!String.valueOf(mActivityModel.getSize().getImageUri()).equals(String.valueOf(mImageUri.getValue()))) {
            //user replaces with a new image
            //delete origin image file
            originFileDelete();
            return getRenameUri();
        } else return mActivityModel.getSize().getImageUri();
    }

    private void originFileDelete() {
        if (mActivityModel.getSize().getImageUri() != null) {
            File originImageFile = new File(Uri.parse(mActivityModel.getSize().getImageUri()).getPath());
            if (originImageFile.delete())
                Log.d(TAG, "inputOutputViewModel : originFileDelete_" + originImageFile.getPath() + "삭제 성공");
            else
                Log.d(TAG, "inputOutputViewModel : originFileDelete_" + originImageFile.getPath() + "삭제 실패");
        }
    }

    private String getRenameUri() {
        File pictureFolderPath = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
        if (mCacheFile.renameTo(pictureFolderPath))
            Log.d(TAG, "inputOutputViewModel : getRenameUri_" + mCacheFile.getPath() + "이동 성동");
        else Log.d(TAG, "inputOutputViewModel : getRenameUri_" + mCacheFile.getPath() + "이동 실패");
        return String.valueOf(Uri.fromFile(pictureFolderPath));
    }

    private void increaseItemAmount() {
        if (mActivityModel.getFolder() == null) {
            mActivityModel.getCategory().setItemAmount(String.valueOf(Integer.parseInt(mActivityModel.getCategory().getItemAmount()) + 1));
            mRepository.categoryUpdate(mActivityModel.getCategory());
        } else {
            mActivityModel.getFolder().setItemAmount(String.valueOf(Integer.parseInt(mActivityModel.getFolder().getItemAmount()) + 1));
            mRepository.folderUpdate(mActivityModel.getFolder());
        }
    }

    private void decreaseItemAmount() {
        if (mActivityModel.getFolder() == null) {
            mActivityModel.getCategory().setItemAmount(String.valueOf(Integer.parseInt(mActivityModel.getCategory().getItemAmount()) - 1));
            mRepository.categoryUpdate(mActivityModel.getCategory());
        } else {
            mActivityModel.getFolder().setItemAmount(String.valueOf(Integer.parseInt(mActivityModel.getFolder().getItemAmount()) - 1));
            mRepository.folderUpdate(mActivityModel.getFolder());
        }
    }
}