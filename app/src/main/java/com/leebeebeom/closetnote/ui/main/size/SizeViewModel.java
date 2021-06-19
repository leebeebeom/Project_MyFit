package com.leebeebeom.closetnote.ui.main.size;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.data.model.model.Size;
import com.leebeebeom.closetnote.data.repository.SizeRepository;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.intuit.sdp.BuildConfig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

import static android.content.ContentValues.TAG;

@HiltViewModel
@Accessors(prefix = "m")
public class SizeViewModel extends ViewModel {
    private static final String JPG = ".jpg";
    private final SizeRepository mSizeRepository;
    private final Application mApplication;
    @Getter
    private final MediatorLiveData<Size> mSizeLive = new MediatorLiveData<>();
    @Getter
    private final LiveData<Set<String>> mAutoCompleteLive;
    private LiveData<Size> mCompareSize;
    @Getter
    private final MediatorLiveData<Uri> mImageUriLive = new MediatorLiveData<>();
    private File mCacheFile;
    private String mFileName;

    @Inject
    public SizeViewModel(Application application,
                         SizeRepository sizeRepository) {
        mApplication = application;
        mSizeRepository = sizeRepository;
        mAutoCompleteLive = mSizeRepository.getBrands();
        Set<String> value = mAutoCompleteLive.getValue();
    }

    public boolean isInputContentsChanged() {
        return mSizeLive.getValue() != null &&
                (mImageUriLive.getValue() != null ||
                        mSizeLive.getValue().isFavorite() ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getBrand()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getName()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getSize()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getLink()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getMemo()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getFirstInfo()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getSecondInfo()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getThirdInfo()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getFourthInfo()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getFirstInfo()) ||
                        !TextUtils.isEmpty(mSizeLive.getValue().getSixthInfo()));
    }

    public boolean isOutputContentsChanged() {
        return mSizeLive.getValue() != null && mCompareSize.getValue() != null &&
                (!String.valueOf(mCompareSize.getValue().getImageUri()).equals(String.valueOf(mImageUriLive.getValue())) ||
                        !mSizeLive.getValue().isFavorite() == mCompareSize.getValue().isFavorite() ||
                        !String.valueOf(mSizeLive.getValue().getBrand()).equals(String.valueOf(mCompareSize.getValue().getBrand())) ||
                        !String.valueOf(mSizeLive.getValue().getName()).equals(String.valueOf(mCompareSize.getValue().getName())) ||
                        !String.valueOf(mSizeLive.getValue().getSize()).equals(String.valueOf(mCompareSize.getValue().getSize())) ||
                        !String.valueOf(mSizeLive.getValue().getLink()).equals(String.valueOf(mCompareSize.getValue().getLink())) ||
                        !String.valueOf(mSizeLive.getValue().getMemo()).equals(String.valueOf(mCompareSize.getValue().getMemo())) ||
                        !String.valueOf(mSizeLive.getValue().getFirstInfo()).equals(String.valueOf(mCompareSize.getValue().getFirstInfo())) ||
                        !String.valueOf(mSizeLive.getValue().getSecondInfo()).equals(String.valueOf(mCompareSize.getValue().getSecondInfo())) ||
                        !String.valueOf(mSizeLive.getValue().getThirdInfo()).equals(String.valueOf(mCompareSize.getValue().getThirdInfo())) ||
                        !String.valueOf(mSizeLive.getValue().getFourthInfo()).equals(String.valueOf(mCompareSize.getValue().getFourthInfo())) ||
                        !String.valueOf(mSizeLive.getValue().getFifthInfo()).equals(String.valueOf(mCompareSize.getValue().getFifthInfo())) ||
                        !String.valueOf(mSizeLive.getValue().getSixthInfo()).equals(String.valueOf(mCompareSize.getValue().getSixthInfo())));
    }

    public void setSizeLive(long parentId, long sizeId, int parentIndex) {
        if (sizeId != -1) {
            mSizeLive.addSource(mSizeRepository.getSizeById(sizeId), mSizeLive::setValue);
            mCompareSize = mSizeRepository.getSizeById(sizeId);
        } else mSizeLive.setValue(new Size(parentIndex, -1, parentId, ""));
        setImageUriLive();
    }

    private void setImageUriLive() {
        mImageUriLive.addSource(mSizeLive, size -> mImageUriLive.setValue(
                size.getImageUri() != null ? Uri.parse(size.getImageUri()) : null));
    }

    public Intent getCropIntent(Uri data) {
        Uri uri = getUri();
        Intent intent = getCropIntent(data, uri);
        //grant
        List<ResolveInfo> resolveInfoList = mApplication.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        resolveInfoList.forEach(resolveInfo -> {
            String packageName = resolveInfo.activityInfo.packageName;
            mApplication.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        });
        return intent;
    }

    public Uri getUri() {
        //file name
        mFileName = CommonUtil.createId() + JPG;

        //make file
        mCacheFile = new File(mApplication.getExternalCacheDir(), mFileName);

        //Uri uri = Uri.fromFile(mCacheFile); <- not working
        String s = mApplication.getPackageName() + ".provider";
        return FileProvider.getUriForFile(mApplication, mApplication.getPackageName() + ".provider", mCacheFile);
    }

    private Intent getCropIntent(Uri data, Uri uri) {
        return new Intent("com.android.camera.action.CROP")
                .setDataAndType(data, "image/*")
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra("aspectX", 3)
                .putExtra("aspectY", 4)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
    }

    public void insertSize() {
        if (mSizeLive.getValue() != null) {
            Size size = mSizeLive.getValue();
            size.setCreatedTime(getCurrentTime());
            size.setModifiedTime("");
            size.setImageUri(mImageUriLive.getValue() == null ? null : getRenameUri());
            mSizeRepository.insert(size);
        }
    }

    public void updateSize() {
        if (mSizeLive.getValue() != null) {
            Size size = mSizeLive.getValue();
            size.setImageUri(getOutputFileSavedUri(size));
            size.setModifiedTime(getCurrentTime());
            mSizeRepository.update(size);
        }
    }

    @NotNull
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(" yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    @Nullable
    private String getOutputFileSavedUri(Size size) {
        //there was a saved image, but user delete it
        if (mImageUriLive.getValue() == null) {
            //delete origin image file
            originFileDelete(size);
            return null;
        } else if (!String.valueOf(size.getImageUri()).equals(String.valueOf(mImageUriLive.getValue()))) {
            //user replaces with a new image
            //delete origin image file
            originFileDelete(size);
            return getRenameUri();
        } else return size.getImageUri();
    }

    private void originFileDelete(Size size) {
        if (size.getImageUri() != null) {
            File originImageFile = new File(Uri.parse(size.getImageUri()).getPath());
            if (originImageFile.delete())
                Log.d(TAG, "inputOutputViewModel : originFileDelete_" + originImageFile.getPath() + "삭제 성공");
            else
                Log.d(TAG, "inputOutputViewModel : originFileDelete_" + originImageFile.getPath() + "삭제 실패");
        }
    }

    @NotNull
    private String getRenameUri() {
        File pictureFolderPath = new File(mApplication.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
        if (mCacheFile.renameTo(pictureFolderPath))
            Log.d(TAG, "inputOutputViewModel : getRenameUri_" + mCacheFile.getPath() + "이동 성동");
        else Log.d(TAG, "inputOutputViewModel : getRenameUri_" + mCacheFile.getPath() + "이동 실패");
        return String.valueOf(Uri.fromFile(pictureFolderPath));
    }
}