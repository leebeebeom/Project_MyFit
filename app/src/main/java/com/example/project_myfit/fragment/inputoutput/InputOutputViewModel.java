package com.example.project_myfit.fragment.inputoutput;

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

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.BOTTOM_LENGTH;
import static com.example.project_myfit.util.MyFitConstant.CHEST;
import static com.example.project_myfit.util.MyFitConstant.HEM;
import static com.example.project_myfit.util.MyFitConstant.LENGTH;
import static com.example.project_myfit.util.MyFitConstant.OPTION1;
import static com.example.project_myfit.util.MyFitConstant.OPTION2;
import static com.example.project_myfit.util.MyFitConstant.OPTION3;
import static com.example.project_myfit.util.MyFitConstant.OPTION4;
import static com.example.project_myfit.util.MyFitConstant.OPTION5;
import static com.example.project_myfit.util.MyFitConstant.OPTION6;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.RISE;
import static com.example.project_myfit.util.MyFitConstant.SHOULDER;
import static com.example.project_myfit.util.MyFitConstant.SLEEVE;
import static com.example.project_myfit.util.MyFitConstant.THIGH;
import static com.example.project_myfit.util.MyFitConstant.TOP;
import static com.example.project_myfit.util.MyFitConstant.WAIST;

public class InputOutputViewModel extends AndroidViewModel {
    private final Repository.SizeRepository mSizeRepository;
    private Size mCompareSize, mNewSize;
    private final MutableLiveData<Uri> mMutableLiveImageUri;
    private File mCacheFile;
    private String mFileName, mParentCategory;
    private long mParentId;
    private Size mOriginSize;
    private List<String> mSizeBrandList;

    public InputOutputViewModel(@NonNull Application application) {
        super(application);
        mSizeRepository = Repository.getSizeRepository(getApplication());
        mMutableLiveImageUri = new MutableLiveData<>();
    }

    public boolean getCompareResult() {
        return isDefaultOutputChanged() || isSizeOutputChanged();
    }

    private boolean isDefaultOutputChanged() {
        return !String.valueOf(mCompareSize.getImageUri()).equals(String.valueOf(mMutableLiveImageUri.getValue())) ||
                !mOriginSize.isFavorite() == mCompareSize.isFavorite() ||
                !String.valueOf(mOriginSize.getBrand()).equals(String.valueOf(mCompareSize.getBrand())) ||
                !String.valueOf(mOriginSize.getName()).equals(String.valueOf(mCompareSize.getName())) ||
                !String.valueOf(mOriginSize.getSize()).equals(String.valueOf(mCompareSize.getSize())) ||
                !String.valueOf(mOriginSize.getLink()).equals(String.valueOf(mCompareSize.getLink())) ||
                !String.valueOf(mOriginSize.getMemo()).equals(String.valueOf(mCompareSize.getMemo()));
    }

    private boolean isSizeOutputChanged() {
        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER)) {
            return !String.valueOf(mOriginSize.getSizeMap().get(LENGTH)).equals(String.valueOf(mCompareSize.getSizeMap().get(LENGTH))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(SHOULDER)).equals(String.valueOf(mCompareSize.getSizeMap().get(SHOULDER))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(CHEST)).equals(String.valueOf(mCompareSize.getSizeMap().get(CHEST))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(SLEEVE)).equals(String.valueOf(mCompareSize.getSizeMap().get(SLEEVE)));
        } else if (mParentCategory.equals(BOTTOM)) {
            return !String.valueOf(mOriginSize.getSizeMap().get(BOTTOM_LENGTH)).equals(String.valueOf(mCompareSize.getSizeMap().get(BOTTOM_LENGTH))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(WAIST)).equals(String.valueOf(mCompareSize.getSizeMap().get(WAIST))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(THIGH)).equals(String.valueOf(mCompareSize.getSizeMap().get(THIGH))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(RISE)).equals(String.valueOf(mCompareSize.getSizeMap().get(RISE))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(HEM)).equals(String.valueOf(mCompareSize.getSizeMap().get(HEM)));
        } else {
            return !String.valueOf(mOriginSize.getSizeMap().get(OPTION1)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION1))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(OPTION2)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION2))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(OPTION3)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION3))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(OPTION4)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION4))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(OPTION5)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION5))) ||
                    !String.valueOf(mOriginSize.getSizeMap().get(OPTION6)).equals(String.valueOf(mCompareSize.getSizeMap().get(OPTION6)));
        }
    }

    public void initResources(long parentId, long sizeId, String parentCategory) {
        this.mOriginSize = mSizeRepository.getSize(sizeId);
        this.mParentCategory = parentCategory;
        this.mParentId = parentId;

        if (mOriginSize != null) {
            mCompareSize = mSizeRepository.getSize(sizeId);
            mMutableLiveImageUri.setValue(mOriginSize.getImageUri() != null ? Uri.parse(mOriginSize.getImageUri()) : null);
        }
    }

    public MutableLiveData<Uri> getMutableImageUri() {
        return mMutableLiveImageUri;
    }

    public Intent getCropIntent(Uri data) {
        //file name
        mFileName = "MyFit_" + getCurrentTime("yyyyMMddHHmmss") + ".jpg";

        //make file
        mCacheFile = new File(getApplication().getExternalCacheDir(), mFileName);

        //Uri uri = Uri.fromFile(mCacheFile); <- No
        Uri uri = FileProvider.getUriForFile(getApplication(), "com.example.project_myfit.provider", mCacheFile);

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

    public void sizeInsert(boolean mIsSearchView) {
        mNewSize.setOrderNumber(mSizeRepository.getSizeLargestOrderPlus1());
        mNewSize.setCreatedTime(getCurrentTime(" yyyy년 MM월 dd일 HH:mm:ss"));
        mNewSize.setModifiedTime("");
        mNewSize.setImageUri(mMutableLiveImageUri.getValue() == null ? null : getRenameUri());
        mNewSize.setParentId(mParentId);
        mSizeRepository.sizeInsert(mNewSize);
        if (mIsSearchView) parentSetDummy();
    }

    private void parentSetDummy() {
        Category category = Repository.getCategoryRepository(getApplication()).getCategory(mParentId);
        if (category != null){
            category.setDummy(!category.getDummy());
            Repository.getCategoryRepository(getApplication()).categoryUpdate(category);
        }else{
            Folder folder = Repository.getFolderRepository(getApplication()).getFolder(mParentId);
            folder.setDummy(!folder.getDummy());
            Repository.getFolderRepository(getApplication()).folderUpdate(folder);
        }
    }

    public void update() {
        mOriginSize.setImageUri(getOutputFileSavedUri());
        mOriginSize.setModifiedTime(getCurrentTime(" yyyy년 MM월 dd일 HH:mm:ss"));
        mSizeRepository.sizeUpdate(mOriginSize);
    }

    public Size getSize( ) {
        if (mOriginSize == null) {
            if (mNewSize == null) mNewSize = new Size(mParentCategory);
            return mNewSize;
        } else return mOriginSize;
    }

    @NotNull
    private String getCurrentTime(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    @Nullable
    private String getOutputFileSavedUri() {
        //there was a saved image, but user delete it
        if (mMutableLiveImageUri.getValue() == null) {
            //delete origin image file
            originFileDelete();
            return null;
        } else if (!String.valueOf(mOriginSize.getImageUri()).equals(String.valueOf(mMutableLiveImageUri.getValue()))) {
            //user replaces with a new image
            //delete origin image file
            originFileDelete();
            return getRenameUri();
        } else return mOriginSize != null ? mOriginSize.getImageUri() : null;
    }

    private void originFileDelete() {
        if (mOriginSize.getImageUri() != null) {
            File originImageFile = new File(Uri.parse(mOriginSize.getImageUri()).getPath());
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

    public List<String> getSizeBrandList() {
        if (mSizeBrandList == null) mSizeBrandList = mSizeRepository.getSizeBrandList();
        return mSizeBrandList;
    }
}