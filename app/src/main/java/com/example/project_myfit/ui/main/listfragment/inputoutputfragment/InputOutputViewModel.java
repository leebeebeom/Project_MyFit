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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private MutableLiveData<Uri> mMutableLiveImageUri;
    private File mCacheFile;
    private String mFileName;
    private MainActivityViewModel mActivityModel;
    private long mFolderId;

    public InputOutputViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public Repository getRepository() {
        return mRepository;
    }

    //dao-------------------------------------------------------------------------------------------
    public void sizeInsert() {
        mNewSize.setOrderNumber(mRepository.getSizeLargestOrder() + 1);
        mNewSize.setCreatedTime(getCurrentTime(" yyyy년 MM월 dd일 HH:mm:ss"));
        mNewSize.setModifiedTime("");
        mNewSize.setImageUri(mMutableLiveImageUri.getValue() == null ? null : getRenameUri());
        mNewSize.setFolderId(mFolderId);
        mNewSize.setParentCategory(mActivityModel.getCategory().getParentCategory());
        mRepository.sizeInsert(mNewSize);
    }

    public void update() {
        mActivityModel.getSize().setImageUri(getOutputFileSavedUri());
        mActivityModel.getSize().setModifiedTime(getCurrentTime(" yyyy년 MM월 dd일 HH:mm:ss"));
        mRepository.sizeUpdate(mActivityModel.getSize());
    }

    public void delete() {
        mActivityModel.getSize().setIsDeleted(1);
        mRepository.sizeUpdate(mActivityModel.getSize());
    }
    //----------------------------------------------------------------------------------------------

    public void checkInputOutput(MainActivityViewModel activityModel) {
        if (mActivityModel == null) mActivityModel = activityModel;
        if (mFolderId == 0)
            mFolderId = mActivityModel.getFolder() == null ? mActivityModel.getCategory().getId() : mActivityModel.getFolder().getId();
        if (mActivityModel.getSize() != null) {
            if (mCompareSize == null)
                mCompareSize = mRepository.getSize(mActivityModel.getSize().getId());
            if (mMutableLiveImageUri == null) {
                mMutableLiveImageUri = new MutableLiveData<>();
                mMutableLiveImageUri.setValue(mActivityModel.getSize().getImageUri() != null ? Uri.parse(mActivityModel.getSize().getImageUri()) : null);
            }
        }
    }

    public boolean getCompareResult() {
        return !String.valueOf(mCompareSize.getImageUri()).equals(String.valueOf(mMutableLiveImageUri.getValue())) ||
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

    public MutableLiveData<Uri> getMutableImageUri() {
        if (mMutableLiveImageUri == null) mMutableLiveImageUri = new MutableLiveData<>();
        return mMutableLiveImageUri;
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
                .putExtra("aspectX", 3)
                .putExtra("aspectY", 4)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);

        //grant
        List<ResolveInfo> resolveInfoList = getApplication().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getApplication().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        return intent;
    }

    @NotNull
    private String getCurrentTime(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    @Nullable
    private String getOutputFileSavedUri() {
        //there was a saved image, but user deleted it
        if (mActivityModel.getSize().getImageUri() != null && mMutableLiveImageUri.getValue() == null) {
            //delete origin image file
            originFileDelete();
            return null;
        } else if (!String.valueOf(mActivityModel.getSize().getImageUri()).equals(String.valueOf(mMutableLiveImageUri.getValue()))) {
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

    @NotNull
    private String getRenameUri() {
        File pictureFolderPath = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
        if (mCacheFile.renameTo(pictureFolderPath))
            Log.d(TAG, "inputOutputViewModel : getRenameUri_" + mCacheFile.getPath() + "이동 성동");
        else Log.d(TAG, "inputOutputViewModel : getRenameUri_" + mCacheFile.getPath() + "이동 실패");
        return String.valueOf(Uri.fromFile(pictureFolderPath));
    }
}