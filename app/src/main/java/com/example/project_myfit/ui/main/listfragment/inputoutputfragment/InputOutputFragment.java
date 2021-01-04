package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Map;

public class InputOutputFragment extends Fragment {
    public static final int GET_IMAGE_REQUEST_CODE = 1000;
    public static final int CROP_REQUEST_CODE = 2000;
    private InputViewModel mModel;
    private FragmentInputOutputBinding mBinding;
    //Back Pressed Call Back
    private final OnBackPressedCallback mCallBack = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (!mModel.isOutput()) setOnBackPressedCallBack();
            else setOnBackPressedCallBackOutput();
        }
    };
    private MainActivityViewModel mActivityModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = new ViewModelProvider(this).get(InputViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        //Set LargestOrder
        mModel.setLargestOrder();

        //if is Output
        if (mActivityModel.getSize() != null) {
            mModel.setIsOutput(true);
            mModel.setSize(mActivityModel.getSize());
            mModel.setOldSize(mActivityModel.getSize().getId());
        } else mModel.setNewSize(new Size());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentInputOutputBinding.inflate(getLayoutInflater());

        //Set Layout
        setLayout();

        //If is Output Set Data
        if (mModel.isOutput()) {
            mBinding.setSize(mModel.getSize());
            mBinding.time.setVisibility(View.VISIBLE);
            //If there's a saved image
            //Add Image Icon GONE
            if (mModel.getOriginFileUri() != null) mBinding.addImageIcon.setVisibility(View.GONE);
        } else mBinding.setSize(mModel.getNewSize());

        //Set Selection
        setSelection();
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
        if (mActivityModel.getCategory().getParentCategory().equals(MyFitConstant.TOP) || mActivityModel.getCategory().getParentCategory().equals(MyFitConstant.OUTER))
            mBinding.inputTopOutput.setVisibility(View.VISIBLE);
        else if (mActivityModel.getCategory().getParentCategory().equals(MyFitConstant.BOTTOM))
            mBinding.inputBottom.setVisibility(View.VISIBLE);
        else mBinding.inputEtc.setVisibility(View.VISIBLE);
    }

    //Set Selection
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

    //Click Listener
    private void setClickListener() {

        //Image Click(Get image)
        mBinding.image.setOnClickListener(v -> getImageUri());

        //Image Long Click(Clear)
        mBinding.image.setOnLongClickListener(v -> {
            if (!mModel.isOutput() && mModel.getCacheFileUri() != null)
                showImageClearDialog();
                //If there's saved image or added image
            else if (mModel.isOutput() && mModel.getOriginFileUri() != null || mModel.getCacheFileUri() != null)
                showImageClearDialog();
            return true;
        });

        //Fab Click(Save)
        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> {
            if (!mModel.isOutput()) {
                mModel.inputFabClick();
                mModel.insert(getSize());
            } else {
                mModel.outputFabClick();
                mModel.update(getUpdateSize());
            }
            Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
        });
        //Go Button Click
        mBinding.goButton.setOnClickListener(v -> {
            Uri uri = Uri.parse(String.valueOf(mBinding.link.getText()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivity(intent);
        });
    }

    //Get Image
    private void getImageUri() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(intent, GET_IMAGE_REQUEST_CODE);
    }

    //Crop
    private void cropImage(Uri data) {
        Intent intent = mModel.getCropIntent(data);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(intent, CROP_REQUEST_CODE);

    }

    //Activity Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            //Get Image
            if (requestCode == GET_IMAGE_REQUEST_CODE) cropImage(data.getData());
                //Crop Image
            else {
                mModel.setCacheFileUri(data.getData());
                mBinding.image.setImageURI(mModel.getCacheFileUri());
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
                    mModel.setOriginFileUri(null);
                    mModel.setCacheFileUri(null);
                    mBinding.image.setImageURI(null);
                    mBinding.addImageIcon.setVisibility(View.VISIBLE);
                });
        builder.show();
    }

    //Make Size
    private Size getSize() {
        int LargestOrder = mModel.getLargestOrder() + 1;
        //If no added image
        String imageUri = mModel.getCacheFileUri() == null ? null : String.valueOf(mModel.getSavedFileUri());
        int folderId = mActivityModel.getCategory().getId();
        int inFolderId = mActivityModel.getListFolder() != null ? mActivityModel.getListFolder().getId() : 0;
        mModel.getNewSize().setOrderNumberSize(LargestOrder);
        mModel.getNewSize().setCreatedTime(mModel.getCurrentTime());
        mModel.getNewSize().setModifiedTime(mModel.getCurrentTime());
        mModel.getNewSize().setImageUri(imageUri);
        mModel.getNewSize().setFolderId(folderId);
        mModel.getNewSize().setInFolderId(inFolderId);
        return mModel.getNewSize();
    }

    //Get Update Size
    private Size getUpdateSize() {
        String imageUri;
        //No saved images and no added images or
        //Saved image is deleted and no added image
        if (mModel.getOriginFileUri() == null && mModel.getCacheFileUri() == null)
            mModel.getSize().setImageUri(null);
        else if (mModel.getCacheFileUri() != null) {
            //If there's a added image
            imageUri = String.valueOf(mModel.getSavedFileUri());
            mModel.getSize().setImageUri(imageUri);
        }
        mModel.getSize().setModifiedTime(mModel.getCurrentTime());
        return mModel.getSize();
    }

    //Back Pressed Call Back setting
    private void setOnBackPressedCallBack() {
        if (mModel.getCacheFileUri() != null ||
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
            Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
    }

    //On Back Pressed Call Back Output
    private void setOnBackPressedCallBackOutput() {
        String brand = mModel.getSize().getBrand() != null ? mModel.getOldSize().getBrand() : "";
        String name = mModel.getSize().getName() != null ? mModel.getOldSize().getName() : "";
        String size = mModel.getSize().getSize() != null ? mModel.getOldSize().getSize() : "";
        String link = mModel.getSize().getLink() != null ? mModel.getOldSize().getLink() : "";
        String memo = mModel.getSize().getMemo() != null ? mModel.getOldSize().getMemo() : "";

        Map<String, String> oldSizeMap = mModel.getOldSize().getSizeMap();
        String length = oldSizeMap.get(MyFitConstant.LENGTH) != null ? oldSizeMap.get(MyFitConstant.LENGTH) : "";
        String shoulder = oldSizeMap.get(MyFitConstant.SHOULDER) != null ? oldSizeMap.get(MyFitConstant.SHOULDER) : "";
        String chest = oldSizeMap.get(MyFitConstant.CHEST) != null ? oldSizeMap.get(MyFitConstant.CHEST) : "";
        String sleeve = oldSizeMap.get(MyFitConstant.SLEEVE) != null ? oldSizeMap.get(MyFitConstant.SLEEVE) : "";
        String bottom_length = oldSizeMap.get(MyFitConstant.BOTTOM_LENGTH) != null ? oldSizeMap.get(MyFitConstant.BOTTOM_LENGTH) : "";
        String waist = oldSizeMap.get(MyFitConstant.WAIST) != null ? oldSizeMap.get(MyFitConstant.WAIST) : "";
        String thigh = oldSizeMap.get(MyFitConstant.THIGH) != null ? oldSizeMap.get(MyFitConstant.THIGH) : "";
        String rise = oldSizeMap.get(MyFitConstant.RISE) != null ? oldSizeMap.get(MyFitConstant.RISE) : "";
        String hem = oldSizeMap.get(MyFitConstant.HEM) != null ? oldSizeMap.get(MyFitConstant.HEM) : "";
        String option1 = oldSizeMap.get(MyFitConstant.OPTION1) != null ? oldSizeMap.get(MyFitConstant.OPTION1) : "";
        String option2 = oldSizeMap.get(MyFitConstant.OPTION2) != null ? oldSizeMap.get(MyFitConstant.OPTION2) : "";
        String option3 = oldSizeMap.get(MyFitConstant.OPTION3) != null ? oldSizeMap.get(MyFitConstant.OPTION3) : "";
        String option4 = oldSizeMap.get(MyFitConstant.OPTION4) != null ? oldSizeMap.get(MyFitConstant.OPTION4) : "";
        String option5 = oldSizeMap.get(MyFitConstant.OPTION5) != null ? oldSizeMap.get(MyFitConstant.OPTION5) : "";
        String option6 = oldSizeMap.get(MyFitConstant.OPTION6) != null ? oldSizeMap.get(MyFitConstant.OPTION6) : "";

        if (//Origin image changed
                !String.valueOf(mModel.getSize().getImageUri()).equals(String.valueOf(mModel.getOriginFileUri())) ||
                        //Image added
                        mModel.getCacheFileUri() != null ||
                        !mModel.getOldSize().isFavorite() == mBinding.checkboxFavorite.isChecked() ||
                        !brand.equals(String.valueOf(mBinding.brand.getText())) ||
                        !name.equals(String.valueOf(mBinding.name.getText())) ||
                        !size.equals(String.valueOf(mBinding.size.getText())) ||
                        !link.equals(String.valueOf(mBinding.link.getText())) ||
                        !memo.equals(String.valueOf(mBinding.memo.getText())) ||
                        !length.equals(String.valueOf(mBinding.length.getText())) ||
                        !shoulder.equals(String.valueOf(mBinding.shoulder.getText())) ||
                        !chest.equals(String.valueOf(mBinding.chest.getText())) ||
                        !sleeve.equals(String.valueOf(mBinding.sleeve.getText())) ||
                        !bottom_length.equals(String.valueOf(mBinding.bottomLength.getText())) ||
                        !waist.equals(String.valueOf(mBinding.waist.getText())) ||
                        !thigh.equals(String.valueOf(mBinding.thigh.getText())) ||
                        !rise.equals(String.valueOf(mBinding.rise.getText())) ||
                        !hem.equals(String.valueOf(mBinding.hem.getText())) ||
                        !option1.equals(String.valueOf(mBinding.option1.getText())) ||
                        !option2.equals(String.valueOf(mBinding.option2.getText())) ||
                        !option3.equals(String.valueOf(mBinding.option3.getText())) ||
                        !option4.equals(String.valueOf(mBinding.option4.getText())) ||
                        !option5.equals(String.valueOf(mBinding.option5.getText())) ||
                        !option6.equals(String.valueOf(mBinding.option6.getText()))) {
            showGoBackConfirmDialog();
        } else
            Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
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