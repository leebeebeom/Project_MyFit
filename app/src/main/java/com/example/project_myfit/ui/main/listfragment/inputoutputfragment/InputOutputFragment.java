package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.MyFitConstant;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentInputOutputBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class InputOutputFragment extends Fragment {
    public static final int GET_IMAGE_REQUEST_CODE = 1000;
    public static final int CROP_REQUEST_CODE = 2000;
    private InputViewModel mModel;
    private FragmentInputOutputBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Uri mCacheFileUri = null;
    private Uri mSavedFileUri, mOldFileUri;
    private String mParentCategory, mFileName;
    private File mCacheFile;
    private boolean mIsOutput;
    private Size mSize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View Model
        mModel = new ViewModelProvider(this).get(InputViewModel.class);
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //Parent Category
        mParentCategory = mActivityModel.getCategory().getParentCategory();
        //if is Output
        if (mActivityModel.getSize() != null) {
            mIsOutput = true;
            mSize = mActivityModel.getSize();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Binding
        mBinding = FragmentInputOutputBinding.inflate(getLayoutInflater());
        //Set Layout
        setLayout();
        //Set Data
        if (mIsOutput) {
            setData();
        }
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Click Listener
        setClickListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Add Back Pressed Call Back
        requireActivity().getOnBackPressedDispatcher().addCallback(this, mCallBack);
    }

    //Back Pressed Call Back Remove
    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack.remove();
    }

    //Set Layout
    private void setLayout() {
        switch (mParentCategory) {
            case MyFitConstant.BOTTOM:
                mBinding.inputViewFlipper.setVisibility(View.GONE);
                mBinding.inputViewFlipper2.setVisibility(View.VISIBLE);
                mBinding.inputViewFlipper2.setDisplayedChild(0);
                break;
            case MyFitConstant.ETC:
                mBinding.inputViewFlipper.setVisibility(View.GONE);
                mBinding.inputViewFlipper2.setVisibility(View.VISIBLE);
                mBinding.inputViewFlipper2.setDisplayedChild(1);
                break;
        }
    }

    //Set Data
    private void setData() {
        Map<String, String> sizeMap = mSize.getSizeMap();

        //Set Image
        //If there's a saved image
        if (mSize.getImageUri() != null) {
            mOldFileUri = Uri.parse(mSize.getImageUri());
            mBinding.addImageIcon.setVisibility(View.GONE);
            mBinding.image.setImageURI(mOldFileUri);
        }
        mBinding.checkboxFavorite.setChecked(mSize.isFavorite());
        mBinding.brand.setText(mSize.getBrand());
        mBinding.name.setText(mSize.getName());
        mBinding.size.setText(mSize.getSize());
        mBinding.link.setText(mSize.getLink());
        mBinding.memo.setText(mSize.getMemo());
        switch (mParentCategory) {
            case MyFitConstant.TOP:
            case MyFitConstant.OUTER:
                mBinding.inputTopOuter.length.setText(sizeMap.get(MyFitConstant.LENGTH));
                mBinding.inputTopOuter.shoulder.setText(sizeMap.get(MyFitConstant.SHOULDER));
                mBinding.inputTopOuter.chest.setText(sizeMap.get(MyFitConstant.CHEST));
                mBinding.inputTopOuter.sleeve.setText(sizeMap.get(MyFitConstant.SLEEVE));
                break;
            case MyFitConstant.BOTTOM:
                mBinding.inputBottom.length.setText(sizeMap.get(MyFitConstant.LENGTH));
                mBinding.inputBottom.waist.setText(sizeMap.get(MyFitConstant.WAIST));
                mBinding.inputBottom.thigh.setText(sizeMap.get(MyFitConstant.THIGH));
                mBinding.inputBottom.rise.setText(sizeMap.get(MyFitConstant.RISE));
                mBinding.inputBottom.hem.setText(sizeMap.get(MyFitConstant.HEM));
                break;
            case MyFitConstant.ETC:
                mBinding.inputEtc.option1.setText(sizeMap.get(MyFitConstant.OPTION1));
                mBinding.inputEtc.option2.setText(sizeMap.get(MyFitConstant.OPTION2));
                mBinding.inputEtc.option3.setText(sizeMap.get(MyFitConstant.OPTION3));
                mBinding.inputEtc.option4.setText(sizeMap.get(MyFitConstant.OPTION4));
                mBinding.inputEtc.option5.setText(sizeMap.get(MyFitConstant.OPTION5));
                mBinding.inputEtc.option6.setText(sizeMap.get(MyFitConstant.OPTION6));
                break;
        }
    }

    //Click Listener
    private void setClickListener() {
        //Image Click
        mBinding.image.setOnClickListener(v -> getImageUri());

        //Image Long Click(Clear)
        mBinding.image.setOnLongClickListener(v -> {
            if (!mIsOutput && mCacheFileUri != null) {
                showImageClearDialog();
            } else if (mIsOutput) {
                //저장된 이미지가 있거나 새로 추가된 이미지가 있다면
                if (mOldFileUri != null || mCacheFileUri != null)
                    showImageClearDialog();
            }
            return true;
        });
        //Fab Click(Save)
        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> {
            if (!mIsOutput) {
                inputFabClick();
                mModel.insert(getSize());
            } else {
                outputFabClick();
                mModel.update(getUpdateSize());
            }
            Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
        });
    }

    //Output Fab Click
    private void outputFabClick() {
        if (mSize.getImageUri() != null && mOldFileUri == null && mCacheFileUri == null) {
            //저장된 이미지가 있었지만 사용자가 삭제한 경우
            //원래 이미지 삭제
            File deleteFile = new File(mSize.getImageUri());
            if (deleteFile.delete()) {
                Log.d(TAG, "outputFabClick: 파일 삭제됨");
            } else {
                Log.d(TAG, "outputFabClick: 파일 삭제 실패");
            }
        } else if (mCacheFileUri != null) {
            //사용자가 새로운 이미지로 교체했을 경우
            File toPath = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
            mSavedFileUri = Uri.fromFile(toPath);
            if (mCacheFile.renameTo(toPath)) {
                Log.d(TAG, "outputFabClick: 파일 이동됨");
            } else {
                Log.d(TAG, "outputFabClick: 파일 이동 실패");
            }
            //원래 이미지 삭제
            if (mSize.getImageUri() != null) {//원래 이미지가 존재했다면
                Uri uri = Uri.parse(mSize.getImageUri());
                File deleteFile = new File(uri.getPath());
                if (deleteFile.delete()) {
                    Log.d(TAG, "outputFabClick: 파일 삭제됨");
                } else {
                    Log.d(TAG, "outputFabClick: 파일 삭제 실패");
                }
            }

        }
    }

    //Input Fab Click
    private void inputFabClick() {
        //새로 추가된 이미지가 있다면
        if (mCacheFileUri != null) {
            //캐시 폴더에서 픽쳐 폴더로
            File toPath = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), mFileName);
            mSavedFileUri = Uri.fromFile(toPath);
            if (mCacheFile.renameTo(toPath)) {
                Log.d(TAG, "setClickListener: 파일 이동됨");
            } else {
                Log.d(TAG, "setClickListener: 파일 이동 실패");
            }
        }
    }

    //Get Image
    private void getImageUri() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, GET_IMAGE_REQUEST_CODE);
        }
    }

    //Crop
    private void cropImage(Uri data) {
        //Current Time
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentDateTime = dateFormat.format(date);
        //File Name
        mFileName = "MyFit_" + currentDateTime + ".jpg";
        //Make File
        mCacheFile = new File(requireContext().getExternalCacheDir(), mFileName);
        //Intent
        Uri uri = Uri.fromFile(mCacheFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*")
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CROP_REQUEST_CODE);
        }
    }

    //Activity Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            //Get Image
            if (requestCode == GET_IMAGE_REQUEST_CODE) {
                cropImage(data.getData());
            }
            //Crop Image
            else {
                mCacheFileUri = data.getData();
                mBinding.image.setImageURI(mCacheFileUri);
                mBinding.addImageIcon.setVisibility(View.GONE);
            }
        }
    }

    //Image Clear Dialog
    private void showImageClearDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("이미지를 삭제하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> {
                    mOldFileUri = null;
                    mCacheFileUri = null;
                    mBinding.image.setImageURI(null);
                    mBinding.addImageIcon.setVisibility(View.VISIBLE);
                });
        builder.show();
    }

    //Make Size
    private Size getSize() {
        int LargestOrder = mModel.getLargestOrder() + 1;
        String imageUri;
        //추가한 이미지가 없을 경우
        if (mCacheFileUri == null) {
            imageUri = null;
        } else {
            //mSavedFileUri = 새로 저장된 경로(캐시x)
            imageUri = String.valueOf(mSavedFileUri);
        }
        String brand = String.valueOf(mBinding.brand.getText());
        String name = String.valueOf(mBinding.name.getText());
        String size = String.valueOf(mBinding.size.getText());
        String link = String.valueOf(mBinding.link.getText());
        String memo = String.valueOf(mBinding.memo.getText());
        int folderId = mActivityModel.getCategory().getId();
        int inFolderId;
        if (mActivityModel.getListFolder() != null) {
            inFolderId = mActivityModel.getListFolder().getId();
        } else {
            inFolderId = 0;
        }
        boolean isFavorite = mBinding.checkboxFavorite.isChecked();

        Map<String, String> sizeMap = new HashMap<>();
        switch (mParentCategory) {
            case MyFitConstant.TOP:
            case MyFitConstant.OUTER:
                sizeMap.put(MyFitConstant.LENGTH, String.valueOf(mBinding.inputTopOuter.length.getText()));
                sizeMap.put(MyFitConstant.SHOULDER, String.valueOf(mBinding.inputTopOuter.shoulder.getText()));
                sizeMap.put(MyFitConstant.CHEST, String.valueOf(mBinding.inputTopOuter.chest.getText()));
                sizeMap.put(MyFitConstant.SLEEVE, String.valueOf(mBinding.inputTopOuter.sleeve.getText()));
                break;
            case MyFitConstant.BOTTOM:
                sizeMap.put(MyFitConstant.LENGTH, String.valueOf(mBinding.inputBottom.length.getText()));
                sizeMap.put(MyFitConstant.WAIST, String.valueOf(mBinding.inputBottom.waist.getText()));
                sizeMap.put(MyFitConstant.THIGH, String.valueOf(mBinding.inputBottom.thigh.getText()));
                sizeMap.put(MyFitConstant.RISE, String.valueOf(mBinding.inputBottom.rise.getText()));
                sizeMap.put(MyFitConstant.HEM, String.valueOf(mBinding.inputBottom.hem.getText()));
                break;
            case MyFitConstant.ETC:
                sizeMap.put(MyFitConstant.OPTION1, String.valueOf(mBinding.inputEtc.option1.getText()));
                sizeMap.put(MyFitConstant.OPTION2, String.valueOf(mBinding.inputEtc.option2.getText()));
                sizeMap.put(MyFitConstant.OPTION3, String.valueOf(mBinding.inputEtc.option3.getText()));
                sizeMap.put(MyFitConstant.OPTION4, String.valueOf(mBinding.inputEtc.option4.getText()));
                sizeMap.put(MyFitConstant.OPTION5, String.valueOf(mBinding.inputEtc.option5.getText()));
                sizeMap.put(MyFitConstant.OPTION6, String.valueOf(mBinding.inputEtc.option6.getText()));
                break;
        }

        return new Size(LargestOrder, imageUri, brand, name, size, link, memo, folderId, inFolderId, isFavorite, sizeMap);
    }

    //Get Update Size
    private Size getUpdateSize() {
        String imageUri;
        //저장된 이미지가 없고 추가된 이미지도 없을 경우 or
        //저장된 이미지를 삭제하고 이미지를 추가하지 않았을 경우
        if (mOldFileUri == null && mCacheFileUri == null) {
            mSize.setImageUri(null);
        } else if (mCacheFileUri != null) {
            //새로운 이미지를 추가했을 경우
            imageUri = String.valueOf(mSavedFileUri);
            mSize.setImageUri(imageUri);
        }
        mSize.setFavorite(mBinding.checkboxFavorite.isChecked());
        mSize.setBrand(String.valueOf(mBinding.brand.getText()));
        mSize.setName(String.valueOf(mBinding.name.getText()));
        mSize.setSize(String.valueOf(mBinding.size.getText()));
        mSize.setLink(String.valueOf(mBinding.link.getText()));
        mSize.setMemo(String.valueOf(mBinding.memo.getText()));
        Map<String, String> sizeMap = new HashMap<>();
        switch (mParentCategory) {
            case MyFitConstant.TOP:
            case MyFitConstant.OUTER:
                sizeMap.put(MyFitConstant.LENGTH, String.valueOf(mBinding.inputTopOuter.length.getText()));
                sizeMap.put(MyFitConstant.SHOULDER, String.valueOf(mBinding.inputTopOuter.shoulder.getText()));
                sizeMap.put(MyFitConstant.CHEST, String.valueOf(mBinding.inputTopOuter.chest.getText()));
                sizeMap.put(MyFitConstant.SLEEVE, String.valueOf(mBinding.inputTopOuter.sleeve.getText()));
            case MyFitConstant.BOTTOM:
                sizeMap.put(MyFitConstant.LENGTH, String.valueOf(mBinding.inputBottom.length.getText()));
                sizeMap.put(MyFitConstant.WAIST, String.valueOf(mBinding.inputBottom.waist.getText()));
                sizeMap.put(MyFitConstant.THIGH, String.valueOf(mBinding.inputBottom.thigh.getText()));
                sizeMap.put(MyFitConstant.RISE, String.valueOf(mBinding.inputBottom.rise.getText()));
                sizeMap.put(MyFitConstant.HEM, String.valueOf(mBinding.inputBottom.hem.getText()));
            case MyFitConstant.ETC:
                sizeMap.put(MyFitConstant.OPTION1, String.valueOf(mBinding.inputEtc.option1.getText()));
                sizeMap.put(MyFitConstant.OPTION2, String.valueOf(mBinding.inputEtc.option2.getText()));
                sizeMap.put(MyFitConstant.OPTION3, String.valueOf(mBinding.inputEtc.option3.getText()));
                sizeMap.put(MyFitConstant.OPTION4, String.valueOf(mBinding.inputEtc.option4.getText()));
                sizeMap.put(MyFitConstant.OPTION5, String.valueOf(mBinding.inputEtc.option5.getText()));
                sizeMap.put(MyFitConstant.OPTION6, String.valueOf(mBinding.inputEtc.option6.getText()));
        }
        mSize.setSizeMap(sizeMap);
        return mSize;
    }

    //Back Pressed Call Back
    private final OnBackPressedCallback mCallBack = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (!mIsOutput) {
                setOnBackPressedCallBack();
            } else {
                setOnBackPressedCallBackOutput();
            }
        }
    };

    //Back Pressed Call Back setting
    private void setOnBackPressedCallBack() {
        if (mCacheFileUri != null ||
                mBinding.checkboxFavorite.isChecked() ||
                !TextUtils.isEmpty(mBinding.brand.getText()) ||
                !TextUtils.isEmpty(mBinding.name.getText()) ||
                !TextUtils.isEmpty(mBinding.size.getText()) ||
                !TextUtils.isEmpty(mBinding.link.getText()) ||
                !TextUtils.isEmpty(mBinding.memo.getText())) {
            showGoBackConfirmDialog();
        } else {
            switch (mParentCategory) {
                case MyFitConstant.TOP:
                case MyFitConstant.OUTER:
                    if (!TextUtils.isEmpty(mBinding.inputTopOuter.length.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputTopOuter.shoulder.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputTopOuter.chest.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputTopOuter.sleeve.getText())) {
                        showGoBackConfirmDialog();
                        break;
                    }
                case MyFitConstant.BOTTOM:
                    if (!TextUtils.isEmpty(mBinding.inputBottom.length.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputBottom.waist.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputBottom.thigh.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputBottom.rise.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputBottom.hem.getText())) {
                        showGoBackConfirmDialog();
                        break;
                    }
                case MyFitConstant.ETC:
                    if (!TextUtils.isEmpty(mBinding.inputEtc.option1.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputEtc.option2.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputEtc.option3.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputEtc.option4.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputEtc.option5.getText()) ||
                            !TextUtils.isEmpty(mBinding.inputEtc.option6.getText())) {
                        showGoBackConfirmDialog();
                        break;
                    }
                default:
                    Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
            }
        }

    }

    //On Back Pressed Call Back Output
    private void setOnBackPressedCallBackOutput() {
        if (//Origin image changed
                !String.valueOf(mSize.getImageUri()).equals(String.valueOf(mOldFileUri)) ||
                        //Image added
                        mCacheFileUri != null ||
                        !mSize.isFavorite() == mBinding.checkboxFavorite.isChecked() ||
                        !mSize.getBrand().equals(String.valueOf(mBinding.brand.getText())) ||
                        !mSize.getName().equals(String.valueOf(mBinding.name.getText())) ||
                        !mSize.getSize().equals(String.valueOf(mBinding.size.getText())) ||
                        !mSize.getLink().equals(String.valueOf(mBinding.link.getText())) ||
                        !mSize.getMemo().equals(String.valueOf(mBinding.memo.getText()))) {
            showGoBackConfirmDialog();
        } else {
            Map<String, String> oldSizeMap = mSize.getSizeMap();
            switch (mParentCategory) {
                case MyFitConstant.TOP:
                case MyFitConstant.OUTER:
                    String length = String.valueOf(oldSizeMap.get(MyFitConstant.LENGTH));
                    String shoulder = String.valueOf(oldSizeMap.get(MyFitConstant.SHOULDER));
                    String chest = String.valueOf(oldSizeMap.get(MyFitConstant.CHEST));
                    String sleeve = String.valueOf(oldSizeMap.get(MyFitConstant.SLEEVE));
                    if (!length.equals(String.valueOf(mBinding.inputTopOuter.length.getText())) ||
                            !shoulder.equals(String.valueOf(mBinding.inputTopOuter.shoulder.getText())) ||
                            !chest.equals(String.valueOf(mBinding.inputTopOuter.chest.getText())) ||
                            !sleeve.equals(String.valueOf(mBinding.inputTopOuter.sleeve.getText()))) {
                        showGoBackConfirmDialog();
                    } else {
                        Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
                    }
                    break;
                case MyFitConstant.BOTTOM:
                    String length2 = String.valueOf(oldSizeMap.get(MyFitConstant.LENGTH));
                    String waist = String.valueOf(oldSizeMap.get(MyFitConstant.WAIST));
                    String thigh = String.valueOf(oldSizeMap.get(MyFitConstant.THIGH));
                    String rise = String.valueOf(oldSizeMap.get(MyFitConstant.RISE));
                    String hem = String.valueOf(oldSizeMap.get(MyFitConstant.HEM));
                    if (!length2.equals(String.valueOf(mBinding.inputBottom.length.getText())) ||
                            !waist.equals(String.valueOf(mBinding.inputBottom.waist.getText())) ||
                            !thigh.equals(String.valueOf(mBinding.inputBottom.thigh.getText())) ||
                            !rise.equals(String.valueOf(mBinding.inputBottom.rise.getText())) ||
                            !hem.equals(String.valueOf(mBinding.inputBottom.hem.getText()))) {
                        showGoBackConfirmDialog();
                    } else {
                        Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
                    }
                    break;
                case MyFitConstant.ETC:
                    String option1 = String.valueOf(oldSizeMap.get(MyFitConstant.OPTION1));
                    String option2 = String.valueOf(oldSizeMap.get(MyFitConstant.OPTION2));
                    String option3 = String.valueOf(oldSizeMap.get(MyFitConstant.OPTION3));
                    String option4 = String.valueOf(oldSizeMap.get(MyFitConstant.OPTION4));
                    String option5 = String.valueOf(oldSizeMap.get(MyFitConstant.OPTION5));
                    String option6 = String.valueOf(oldSizeMap.get(MyFitConstant.OPTION6));
                    if (!option1.equals(String.valueOf(mBinding.inputEtc.option1.getText())) ||
                            !option2.equals(String.valueOf(mBinding.inputEtc.option2.getText())) ||
                            !option3.equals(String.valueOf(mBinding.inputEtc.option3.getText())) ||
                            !option4.equals(String.valueOf(mBinding.inputEtc.option4.getText())) ||
                            !option5.equals(String.valueOf(mBinding.inputEtc.option5.getText())) ||
                            !option6.equals(String.valueOf(mBinding.inputEtc.option6.getText()))) {
                        showGoBackConfirmDialog();
                    } else {
                        Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
                    }
                    break;
            }
        }
    }

    //On Back Dialog
    private void showGoBackConfirmDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("저장되지 않았습니다.\n종료하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment));
        builder.show();
    }

}