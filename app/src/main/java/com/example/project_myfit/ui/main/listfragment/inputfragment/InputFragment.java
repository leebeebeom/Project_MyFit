package com.example.project_myfit.ui.main.listfragment.inputfragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentInputBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InputFragment extends Fragment {
    public static final int GET_IMAGE_REQUEST_CODE = 1000;
    public static final int CROP = 2000;
    private InputViewModel mModel;
    private FragmentInputBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private String category;
    private Uri mCropImageUri;
    //TODO
    //코드 정리

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), mCallback);
        //Binding
        mBinding = FragmentInputBinding.inflate(getLayoutInflater());
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //Set Layout
        setLayout();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Init
        init();
        //Get Image
        mBinding.image.setOnClickListener(v -> getImageUri());
        //Clear Image
        mBinding.image.setOnLongClickListener(v -> {
            if (mCropImageUri != null) {
                showImageClearDialog();
            }
            return true;
        });
        //Save
        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> {
            mModel.insert(getSize());
            Navigation.findNavController(requireView()).navigate(R.id.action_inputFragment_to_listFragment);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback.remove();
    }

    private void showImageClearDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("이미지를 삭제하시겠습니까?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DONE", (dialog, which) -> {
                    mCropImageUri = null;
                    mBinding.image.setImageURI(null);
                    mBinding.addImageIcon.setVisibility(View.VISIBLE);
                });
        builder.show();
    }

    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(InputViewModel.class);
        //Set Largest Order
        mModel.setLargestOrder();
    }

    //Set Layout
    private void setLayout() {
        category = mActivityModel.getCategory().getParentCategory();
        switch (category) {
            case "TOP":
            case "OUTER":
                mBinding.inputViewFlipper.setDisplayedChild(0);
                break;
            case "BOTTOM":
                mBinding.inputViewFlipper.setVisibility(View.GONE);
                mBinding.inputViewFlipper2.setVisibility(View.VISIBLE);
                mBinding.inputViewFlipper2.setDisplayedChild(0);
                break;
            case "ETC":
                mBinding.inputViewFlipper.setVisibility(View.GONE);
                mBinding.inputViewFlipper2.setVisibility(View.VISIBLE);
                mBinding.inputViewFlipper2.setDisplayedChild(1);
                break;
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
        File file = new File(requireContext().getExternalFilesDir(null), "my_fit.jpg");
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*")
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra("scale", true)
                .putExtra("return-data", true)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CROP);
        }
    }

    //Intent Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == GET_IMAGE_REQUEST_CODE) {
                cropImage(data.getData());
            } else if (requestCode == CROP) {
                mBinding.image.setImageURI(null);
                mCropImageUri = data.getData();
                mBinding.image.setImageURI(mCropImageUri);
                mBinding.addImageIcon.setVisibility(View.GONE);
            }
        }
    }

    //Make Size
    private Size getSize() {
        int LargestOrder = mModel.getLargestOrder() + 1;
        String imageUri = String.valueOf(mCropImageUri);
        String brand = String.valueOf(mBinding.brand.getText());
        String name = String.valueOf(mBinding.name.getText());
        String size = String.valueOf(mBinding.size.getText());
        String link = String.valueOf(mBinding.link.getText());
        String meme = String.valueOf(mBinding.memo.getText());
        int folderId = mActivityModel.getCategory().getId();
        int inFolderId;
        if (mActivityModel.getListFolder() != null) {
            inFolderId = mActivityModel.getListFolder().getId();
        } else {
            inFolderId = 0;
        }
        boolean isFavorite = mBinding.checkboxFavorite.isChecked();
        Map<String, String> sizeMap = new HashMap<>();
        switch (category) {
            case "TOP":
            case "OUTER":
                sizeMap.put("length", String.valueOf(mBinding.inputTopOuter.length.getText()));
                sizeMap.put("shoulder", String.valueOf(mBinding.inputTopOuter.shoulder.getText()));
                sizeMap.put("chest", String.valueOf(mBinding.inputTopOuter.chest.getText()));
                sizeMap.put("sleeve", String.valueOf(mBinding.inputTopOuter.sleeve.getText()));
                break;
            case "BOTTOM":
                sizeMap.put("length", String.valueOf(mBinding.inputBottom.length.getText()));
                sizeMap.put("waist", String.valueOf(mBinding.inputBottom.waist.getText()));
                sizeMap.put("thigh", String.valueOf(mBinding.inputBottom.thigh.getText()));
                sizeMap.put("rise", String.valueOf(mBinding.inputBottom.rise.getText()));
                sizeMap.put("hem", String.valueOf(mBinding.inputBottom.hem.getText()));
                break;
            case "ETC":
                sizeMap.put("option1", String.valueOf(mBinding.inputEtc.option1.getText()));
                sizeMap.put("option2", String.valueOf(mBinding.inputEtc.option2.getText()));
                sizeMap.put("option3", String.valueOf(mBinding.inputEtc.option3.getText()));
                sizeMap.put("option4", String.valueOf(mBinding.inputEtc.option4.getText()));
                sizeMap.put("option5", String.valueOf(mBinding.inputEtc.option5.getText()));
                sizeMap.put("option6", String.valueOf(mBinding.inputEtc.option6.getText()));
                break;
        }
        return new Size(LargestOrder, imageUri, brand, name, size, link, meme, folderId, inFolderId, isFavorite, sizeMap);
    }

    //OnBackPressedCallBack
    private final OnBackPressedCallback mCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            showOnBackDialog();
        }
    };

    private void showOnBackDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("저장되지 않았습니다.\n취소하시겠습니까?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DONE", (dialog, which) -> {
                    Navigation.findNavController(requireView()).navigate(R.id.action_inputFragment_to_listFragment);
                    dialog.dismiss();
                });
        builder.show();
    }
}