package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.example.project_myfit.MyFitConstant.CROP_REQUEST_CODE;
import static com.example.project_myfit.MyFitConstant.GET_IMAGE_REQUEST_CODE;

public class InputOutputFragment extends Fragment {
    private MainActivityViewModel mActivityModel;
    private InputOutputViewModel mModel;
    private FragmentInputOutputBinding mBinding;
    private FloatingActionButton mActivityFab;
    private OnBackPressedCallback mCallBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(InputOutputViewModel.class);
        setHasOptionsMenu(true);

        onBackPressedCallBackInit();
    }

    //onBackPressedCallBack-------------------------------------------------------------------------
    private void onBackPressedCallBackInit() {
        mCallBack = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!mModel.isOutput()) setOnBackPressedCallBackInput();
                else setOnBackPressedCallBackOutput();
            }
        };
    }

    private void setOnBackPressedCallBackInput() {
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
            goListFragment();
    }

    private void setOnBackPressedCallBackOutput() {
        //String.valueOf = "null"
        String brand = mModel.getOldSize().getBrand() != null ? mModel.getOldSize().getBrand() : "";
        String name = mModel.getOldSize().getName() != null ? mModel.getOldSize().getName() : "";
        String size = mModel.getOldSize().getSize() != null ? mModel.getOldSize().getSize() : "";
        String link = mModel.getOldSize().getLink() != null ? mModel.getOldSize().getLink() : "";
        String memo = mModel.getOldSize().getMemo() != null ? mModel.getOldSize().getMemo() : "";

        Map<String, String> oldSizeMap = mModel.getOldSize().getSizeMap();
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

        if (!String.valueOf(mModel.getSize().getImageUri()).equals(String.valueOf(mModel.getOriginFileUri())) || //origin image change check
                mModel.getCacheFileUri() != null ||//image add check
                !mModel.getOldSize().isFavorite() == mBinding.checkboxFavorite.isChecked() ||
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

        setSelection();

        return mBinding.getRoot();
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
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mActivityFab = requireActivity().findViewById(R.id.activity_fab);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), mCallBack);

        setLayout();

        checkInputOutput();

        setData();
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

    private void checkInputOutput() {
        if (mActivityModel.getSize() != null) {
            mModel.setIsOutput(true);
            mModel.setSize(mActivityModel.getSize());
            mModel.setOldSize(mActivityModel.getSize().getId());
        }
        //if is input
        else {
            mModel.setNewSize(new Size());
            mModel.setLargestOrder();
        }
    }

    private void setData() {
        if (mModel.isOutput()) {
            mBinding.setSize(mModel.getSize());
            mBinding.timeLayout.setVisibility(View.VISIBLE);
            //if there's a saved image
            //addImageIcon GONE
            if (mModel.getOriginFileUri() != null) mBinding.addImageIcon.setVisibility(View.GONE);
        }
        //two way data biding
        else mBinding.setSize(mModel.getNewSize());
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

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack.remove();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            //get image result
            if (requestCode == GET_IMAGE_REQUEST_CODE) cropImage(data.getData());
                //cropImage result
            else {
                mModel.setCacheFileUri(data.getData());
                mBinding.image.setImageURI(mModel.getCacheFileUri());
                mBinding.addImageIcon.setVisibility(View.GONE);
            }
        }
    }

    private Size getNewSize() {
        int orderNumber = mModel.getLargestOrder() + 1;
        //check added image
        String imageUri = mModel.getCacheFileUri() == null ? null : String.valueOf(mModel.getSavedFileUri());
        int folderId = mActivityModel.getCategory().getId();
        long inFolderId = mActivityModel.getListFolder() != null ? mActivityModel.getListFolder().getId() : 0;
        mModel.getNewSize().setOrderNumber(orderNumber);
        String pattern = " yyyy년 MM월 dd일 HH:mm:ss";
        mModel.getNewSize().setCreatedTime(mModel.getCurrentTime(pattern));
        mModel.getNewSize().setModifiedTime("");
        mModel.getNewSize().setImageUri(imageUri);
        mModel.getNewSize().setFolderId(folderId);
        mModel.getNewSize().setInFolderId(inFolderId);
        return mModel.getNewSize();
    }

    private Size getUpdatedSize() {
        String imageUri;
        //no saved images and no added images or
        //saved image is deleted
        if (mModel.getOriginFileUri() == null && mModel.getCacheFileUri() == null)
            mModel.getSize().setImageUri(null);
            //there's a added image
        else if (mModel.getCacheFileUri() != null) {
            imageUri = String.valueOf(mModel.getSavedFileUri());
            mModel.getSize().setImageUri(imageUri);
        }
        String pattern = " yyyy년 MM월 dd일 HH:mm:ss";
        mModel.getSize().setModifiedTime(mModel.getCurrentTime(pattern));
        return mModel.getSize();
    }

    private void goListFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_inputOutputFragment_to_listFragment);
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
            if (!mModel.isOutput() && mModel.getCacheFileUri() != null)
                showImageClearDialog();
                //output & there's a saved image or added image
            else if (mModel.isOutput() && mModel.getOriginFileUri() != null || mModel.getCacheFileUri() != null)
                showImageClearDialog();
            return true;
        });
    }

    private void fabClick() {
        mActivityFab.setOnClickListener(v -> {
            if (!mModel.isOutput()) {
                mModel.inputFabClick();
                mModel.insert(getNewSize());
            } else {
                mModel.outputFabClick();
                mModel.update(getUpdatedSize());
            }
            Toast.makeText(requireContext(), "저장됨", Toast.LENGTH_SHORT).show();
            goListFragment();
        });
    }

    private void cropImage(Uri data) {
        Intent intent = mModel.getCropIntent(data);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(intent, CROP_REQUEST_CODE);
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


    //dialog----------------------------------------------------------------------------------------
    private void showGoBackConfirmDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("저장되지 않았습니다.\n종료하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> goListFragment());
        builder.show();
    }

    private void showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("삭제하시겠습니까?\n삭제 후 복구할 수 없습니다")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> {
                    mModel.delete(mActivityModel.getSize());
                    Toast.makeText(requireContext(), "삭제됨", Toast.LENGTH_SHORT).show();
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
                    mModel.setOriginFileUri(null);
                    mModel.setCacheFileUri(null);
                    mBinding.image.setImageURI(null);
                    mBinding.addImageIcon.setVisibility(View.VISIBLE);
                });
        builder.show();
    }
    //----------------------------------------------------------------------------------------------


    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        if (!mModel.isOutput()) menu.getItem(1).setVisible(false);
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