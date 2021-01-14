package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.BuildConfig;
import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.MyFitConstant;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentInputOutputBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.project_myfit.MyFitConstant.CROP_REQUEST_CODE;
import static com.example.project_myfit.MyFitConstant.GET_IMAGE_REQUEST_CODE;

public class InputOutputFragment extends Fragment {
    private MainActivityViewModel mActivityModel;
    private InputOutputViewModel mModel;
    private FragmentInputOutputBinding mBinding;
    private FloatingActionButton mActivityFab;
    private OnBackPressedCallback mOnBackPressedCallBack;
    private Uri mCacheFileUri, mSavedFileUri, mOriginFileUri;
    private String mFileName;
    private File mCacheFile;
    private Size mSize, mOldSize, mNewSize;
    private boolean isOutput;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(InputOutputViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        checkInputOutput();

        mOnBackPressedCallBack = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isOutput) setOnBackPressedCallBackInput();
                else setOnBackPressedCallBackOutput();
            }
        };

        setHasOptionsMenu(true);
    }

    private void checkInputOutput() {
        if (mActivityModel.getSize() != null) {//if is output
            isOutput = true;
            mSize = mActivityModel.getSize();
            mOldSize = mModel.getOldSize(mActivityModel.getSize().getId());
            mOriginFileUri = mSize.getImageUri() != null ? Uri.parse(mSize.getImageUri()) : null;
        } else mNewSize = new Size(); // is not output
    }

    //onBackPressedCallBack-------------------------------------------------------------------------
    private void setOnBackPressedCallBackInput() {
        if (mCacheFileUri != null ||//there's no added image
                mBinding.checkboxFavorite.isChecked() ||
                !TextUtils.isEmpty(mBinding.brand.getText()) ||
                !TextUtils.isEmpty(mBinding.name.getText()) ||
                !TextUtils.isEmpty(mBinding.size.getText()) ||
                !TextUtils.isEmpty(mBinding.link.getText()) ||
                !TextUtils.isEmpty(mBinding.memo.getText()) ||
                !TextUtils.isEmpty(mBinding.length.getText()) ||
                !TextUtils.isEmpty(mBinding.shoulder.getText()) ||
                !TextUtils.isEmpty(mBinding.chest.getText()) ||
                !TextUtils.isEmpty(mBinding.sleeve.getText()) ||
                !TextUtils.isEmpty(mBinding.bottomLength.getText()) ||
                !TextUtils.isEmpty(mBinding.waist.getText()) ||
                !TextUtils.isEmpty(mBinding.thigh.getText()) ||
                !TextUtils.isEmpty(mBinding.rise.getText()) ||
                !TextUtils.isEmpty(mBinding.hem.getText()) ||
                !TextUtils.isEmpty(mBinding.option1.getText()) ||
                !TextUtils.isEmpty(mBinding.option2.getText()) ||
                !TextUtils.isEmpty(mBinding.option3.getText()) ||
                !TextUtils.isEmpty(mBinding.option4.getText()) ||
                !TextUtils.isEmpty(mBinding.option5.getText()) ||
                !TextUtils.isEmpty(mBinding.option6.getText())) {
            showGoBackConfirmDialog();
        } else
            goListFragment();
    }

    private void setOnBackPressedCallBackOutput() {
        //String.valueOf = "null"
        String brand = mOldSize.getBrand() != null ? mOldSize.getBrand() : "";
        String name = mOldSize.getName() != null ? mOldSize.getName() : "";
        String size = mOldSize.getSize() != null ? mOldSize.getSize() : "";
        String link = mOldSize.getLink() != null ? mOldSize.getLink() : "";
        String memo = mOldSize.getMemo() != null ? mOldSize.getMemo() : "";

        Map<String, String> oldSizeMap = mOldSize.getSizeMap();
        String length = oldSizeMap.getOrDefault(MyFitConstant.LENGTH, "");
        String shoulder = oldSizeMap.getOrDefault(MyFitConstant.SHOULDER, "");
        String chest = oldSizeMap.getOrDefault(MyFitConstant.CHEST, "");
        String sleeve = oldSizeMap.getOrDefault(MyFitConstant.SLEEVE, "");
        String bottom_length = oldSizeMap.getOrDefault(MyFitConstant.BOTTOM_LENGTH, "");
        String waist = oldSizeMap.getOrDefault(MyFitConstant.WAIST, "");
        String thigh = oldSizeMap.getOrDefault(MyFitConstant.THIGH, "");
        String rise = oldSizeMap.getOrDefault(MyFitConstant.RISE, "");
        String hem = oldSizeMap.getOrDefault(MyFitConstant.HEM, "");
        String option1 = oldSizeMap.getOrDefault(MyFitConstant.OPTION1, "");
        String option2 = oldSizeMap.getOrDefault(MyFitConstant.OPTION2, "");
        String option3 = oldSizeMap.getOrDefault(MyFitConstant.OPTION3, "");
        String option4 = oldSizeMap.getOrDefault(MyFitConstant.OPTION4, "");
        String option5 = oldSizeMap.getOrDefault(MyFitConstant.OPTION5, "");
        String option6 = oldSizeMap.getOrDefault(MyFitConstant.OPTION6, "");

        if (!String.valueOf(mSize.getImageUri()).equals(String.valueOf(mOriginFileUri)) || //origin image change check
                mCacheFileUri != null ||//image add check
                !mOldSize.isFavorite() == mBinding.checkboxFavorite.isChecked() ||
                !brand.equals(String.valueOf(mBinding.brand.getText())) ||
                !name.equals(String.valueOf(mBinding.name.getText())) ||
                !size.equals(String.valueOf(mBinding.size.getText())) ||
                !link.equals(String.valueOf(mBinding.link.getText())) ||
                !memo.equals(String.valueOf(mBinding.memo.getText())) ||
                !String.valueOf(mBinding.length.getText()).equals(length) ||
                !String.valueOf(mBinding.shoulder.getText()).equals(shoulder) ||
                !String.valueOf(mBinding.chest.getText()).equals(chest) ||
                !String.valueOf(mBinding.sleeve.getText()).equals(sleeve) ||
                !String.valueOf(mBinding.bottomLength.getText()).equals(bottom_length) ||
                !String.valueOf(mBinding.waist.getText()).equals(waist) ||
                !String.valueOf(mBinding.thigh.getText()).equals(thigh) ||
                !String.valueOf(mBinding.rise.getText()).equals(rise) ||
                !String.valueOf(mBinding.hem.getText()).equals(hem) ||
                !String.valueOf(mBinding.option1.getText()).equals(option1) ||
                !String.valueOf(mBinding.option2.getText()).equals(option2) ||
                !String.valueOf(mBinding.option3.getText()).equals(option3) ||
                !String.valueOf(mBinding.option4.getText()).equals(option4) ||
                !String.valueOf(mBinding.option5.getText()).equals(option5) ||
                !String.valueOf(mBinding.option6.getText()).equals(option6)) {
            showGoBackConfirmDialog();
        } else
            goListFragment();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentInputOutputBinding.inflate(getLayoutInflater());
        mActivityFab = requireActivity().findViewById(R.id.activity_fab);

        setLayout();
        setData();
        setSelection();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), mOnBackPressedCallBack);
        return mBinding.getRoot();
    }

    private void setLayout() {
        //TOP or OUTER
        if (mActivityModel.getCategory().getParentCategory().equals(MyFitConstant.TOP) || mActivityModel.getCategory().getParentCategory().equals(MyFitConstant.OUTER))
            mBinding.inputTopOutput.setVisibility(View.VISIBLE);
            //BOTTOM
        else if (mActivityModel.getCategory().getParentCategory().equals(MyFitConstant.BOTTOM))
            mBinding.inputBottom.setVisibility(View.VISIBLE);
            //ETC
        else mBinding.inputEtc.setVisibility(View.VISIBLE);
    }

    private void setData() {
        if (isOutput) {
            mBinding.setSize(mSize);
            mBinding.timeLayout.setVisibility(View.VISIBLE);
            //if there's a saved image
            //addImageIcon GONE
            if (mOriginFileUri != null) mBinding.addIcon.setVisibility(View.GONE);
        }
        //two way data biding
        else mBinding.setSize(mNewSize);
    }

    private void setSelection() {
        mBinding.brand.setSelection(mBinding.brand.length());
        mBinding.name.setSelection(mBinding.name.length());
        mBinding.size.setSelection(mBinding.size.length());
        mBinding.link.setSelection(mBinding.link.length());
        mBinding.memo.setSelection(mBinding.memo.length());
        mBinding.length.setSelection(mBinding.length.length());
        mBinding.shoulder.setSelection(mBinding.shoulder.length());
        mBinding.chest.setSelection(mBinding.chest.length());
        mBinding.sleeve.setSelection(mBinding.sleeve.length());
        mBinding.bottomLength.setSelection(mBinding.bottomLength.length());
        mBinding.waist.setSelection(mBinding.waist.length());
        mBinding.thigh.setSelection(mBinding.thigh.length());
        mBinding.rise.setSelection(mBinding.rise.length());
        mBinding.hem.setSelection(mBinding.hem.length());
        mBinding.option1.setSelection(mBinding.option1.length());
        mBinding.option2.setSelection(mBinding.option2.length());
        mBinding.option3.setSelection(mBinding.option3.length());
        mBinding.option4.setSelection(mBinding.option4.length());
        mBinding.option5.setSelection(mBinding.option5.length());
        mBinding.option6.setSelection(mBinding.option6.length());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setClickListener();
    }

    private void setClickListener() {
        imageClick();

        imageLongClick();

        fabClick();

        goButtonClick();
    }

    //click-----------------------------------------------------------------------------------------
    private void imageClick() {
        mBinding.image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivityForResult(intent, GET_IMAGE_REQUEST_CODE);
        });
    }

    private void imageLongClick() {
        mBinding.image.setOnLongClickListener(v -> {
            //input & there's a added image
            if (!isOutput && mCacheFileUri != null)
                showImageClearDialog();
                //output & there's a saved image or added image
            else if (isOutput && mOriginFileUri != null || mCacheFileUri != null)
                showImageClearDialog();
            return true;
        });
    }

    private void fabClick() {
        mActivityFab.setOnClickListener(v -> {
            if (!isOutput) {
                inputFileSave();
                mModel.insert(getNewSize());
            } else {
                outputFileSave();
                mModel.update(getUpdatedSize());
            }
            goListFragment();
        });
    }

    public void inputFileSave() {
        //if there's a added image
        if (mCacheFileUri != null) {
            //file move cache folder to picture folder
            File pictureFolderPath = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
            mSavedFileUri = Uri.fromFile(pictureFolderPath);
            if (mCacheFile.renameTo(pictureFolderPath))
                Log.d(TAG, "inputFabClick: file rename success");
            else Log.d(TAG, "inputFabClick: file rename fail");
        }
    }

    public void outputFileSave() {
        //there was a saved image, but user deleted it
        if (mSize.getImageUri() != null && mOriginFileUri == null && mCacheFileUri == null) {
            //delete origin image file
            File OriginImageFile = new File(Uri.parse(mSize.getImageUri()).getPath());
            if (OriginImageFile.delete()) Log.d(TAG, "outputFabClick: file delete success");
            else Log.d(TAG, "outputFabClick: file delete fail");
        } else if (mCacheFileUri != null) {
            //user replaces with a new image
            //file move cache folder to picture folder
            File pictureFolderPath = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
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

    private void goButtonClick() {
        mBinding.goButton.setOnClickListener(v -> {
            Uri link = Uri.parse(String.valueOf(mBinding.link.getText()));
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivity(intent);
        });
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            //get image result
            if (requestCode == GET_IMAGE_REQUEST_CODE) cropImage(data.getData());
                //cropImage result
            else {
                mCacheFileUri = data.getData();
                mBinding.image.setImageURI(mCacheFileUri);
                mBinding.addIcon.setVisibility(View.GONE);
            }
        }
    }

    private void cropImage(Uri data) {
        //file name
        mFileName = "MyFit_" + getCurrentTime("yyyyMMddHHmmss") + ".jpg";

        //make file
        mCacheFile = new File(requireContext().getExternalCacheDir(), mFileName);

        //intent
//        Uri uri = Uri.fromFile(mCacheFile); <- No
        Uri uri = FileProvider.getUriForFile(requireContext().getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", mCacheFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*")
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> resolveInfoList = requireActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            requireActivity().grantUriPermission(packageName, uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private Size getNewSize() {
        int orderNumber = mModel.getLargestOrder() + 1;
        //check added image
        String imageUri = mCacheFileUri == null ? null : String.valueOf(mSavedFileUri);
        long folderId = mActivityModel.getFolder() == null ?
                mActivityModel.getCategory().getId() : mActivityModel.getFolder().getId();
        mNewSize.setOrderNumber(orderNumber);
        mNewSize.setCreatedTime(getCurrentTime(" yyyy년 MM월 dd일 HH:mm:ss"));
        mNewSize.setModifiedTime("");
        mNewSize.setImageUri(imageUri);
        mNewSize.setFolderId(folderId);
        return mNewSize;
    }

    private Size getUpdatedSize() {
        String imageUri;
        //there's no saved images and no added images or
        //saved image is deleted
        if (mOriginFileUri == null && mCacheFileUri == null)
            mSize.setImageUri(null);
            //there's a added image
        else if (mCacheFileUri != null) {
            imageUri = String.valueOf(mSavedFileUri);
            mSize.setImageUri(imageUri);
        }
        String pattern = " yyyy년 MM월 dd일 HH:mm:ss";
        mSize.setModifiedTime(getCurrentTime(pattern));
        return mSize;
    }

    private void goListFragment() {
        mOnBackPressedCallBack.remove();
        requireActivity().onBackPressed();
    }

    public String getCurrentTime(String pattern) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(date);
    }


    //dialog----------------------------------------------------------------------------------------
    private void showGoBackConfirmDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("저장되지 않았습니다.\n종료하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> goListFragment());
        builder.show();
    }

    @SuppressLint("ShowToast")
    private void showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("휴지통으로 이동하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> {
                    mModel.delete(mSize);
                    goListFragment();
                });
        builder.show();
    }

    private void showImageClearDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("이미지를 삭제하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> {
                    mOriginFileUri = null;
                    mCacheFileUri = null;
                    mBinding.image.setImageURI(null);
                    mBinding.addIcon.setVisibility(View.VISIBLE);
                });
        builder.show();
    }
    //----------------------------------------------------------------------------------------------


    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        if (!isOutput) menu.getItem(1).setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.input_output_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.input_output_save) {
            mActivityFab.callOnClick();
            return true;
        } else if (item.getItemId() == R.id.input_output_delete) {
            showDeleteConfirmDialog();
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
}