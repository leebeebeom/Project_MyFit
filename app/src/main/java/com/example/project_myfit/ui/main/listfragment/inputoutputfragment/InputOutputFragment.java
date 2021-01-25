package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.MyFitConstant;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentInputOutputBinding;
import com.example.project_myfit.dialog.DeleteConFirmDialog;
import com.example.project_myfit.dialog.GoBackConfirmDialog;
import com.example.project_myfit.dialog.ImageClearDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CROP_REQUEST_CODE;
import static com.example.project_myfit.MyFitConstant.GET_IMAGE_REQUEST_CODE;

public class InputOutputFragment extends Fragment implements GoBackConfirmDialog.GoBackConfirmClick, DeleteConFirmDialog.DeleteConfirmClick, ImageClearDialog.ImageClearConfirmClick {
    private InputOutputViewModel mModel;
    private MainActivityViewModel mActivityModel;
    private FragmentInputOutputBinding mBinding;
    private FloatingActionButton mActivityFab;
    private OnBackPressedCallback mOnBackPressedCallBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(InputOutputViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        //check input output
        mModel.checkInputOutput(mActivityModel);

        mOnBackPressedCallBack = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mActivityModel.getSize() == null) setOnBackPressedCallBackInput();
                else setOnBackPressedCallBackOutput();
            }
        };

        setHasOptionsMenu(true);
    }

    //onBackPressedCallBack-------------------------------------------------------------------------
    private void setOnBackPressedCallBackInput() {
        if (mModel.getImageUri().getValue() != null ||
                mModel.getSize().isFavorite() ||
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
            showDialog(new GoBackConfirmDialog());
        } else goListFragment();
    }

    private void setOnBackPressedCallBackOutput() {
        if (mModel.getCompareResult()) showDialog(new GoBackConfirmDialog());
        else goListFragment();
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

        return mBinding.getRoot();
    }

    private void setLayout() {
        String parentCategory = mActivityModel.getCategory().getParentCategory();
        //TOP or OUTER
        if (parentCategory.equals(MyFitConstant.TOP) || parentCategory.equals(MyFitConstant.OUTER))
            mBinding.inputTopOuter.setVisibility(View.VISIBLE);
            //BOTTOM
        else if (parentCategory.equals(MyFitConstant.BOTTOM))
            mBinding.inputBottom.setVisibility(View.VISIBLE);
            //ETC
        else mBinding.inputEtc.setVisibility(View.VISIBLE);
    }

    private void setData() {
        mBinding.setSize(mModel.getSize());
        if (mActivityModel.getSize() != null) mBinding.timeLayout.setVisibility(View.VISIBLE);
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

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), mOnBackPressedCallBack);

        mModel.getImageUri().observe(getViewLifecycleOwner(), uri -> {
            mBinding.image.setImageURI(uri);
            if (uri != null) mBinding.addIcon.setVisibility(View.GONE);
        });

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
            if (mModel.getImageUri().getValue() != null) showDialog(new ImageClearDialog());
            return true;
        });
    }

    private void fabClick() {

        mActivityFab.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mBinding.brand.getText()) && TextUtils.isEmpty(mBinding.name.getText())) {
                mBinding.brandLayout.setError("브랜드는 필수 입력 항목입니다.");
                mBinding.brand.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s)) mBinding.brandLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                mBinding.nameLayout.setError("이름은 필수 입력 항목입니다.");
                mBinding.name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s)) mBinding.nameLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else if (TextUtils.isEmpty(mBinding.brand.getText()) || TextUtils.isEmpty(mBinding.name.getText())) {
                if (TextUtils.isEmpty(mBinding.brand.getText())) {
                    mBinding.brandLayout.setError("브랜드는 필수 입력 항목입니다.");
                    mBinding.brand.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (!TextUtils.isEmpty(s)) mBinding.brandLayout.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } else {
                    mBinding.nameLayout.setError("이름은 필수 입력 항목입니다.");
                    mBinding.name.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (!TextUtils.isEmpty(s)) mBinding.nameLayout.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            } else {
                if (mActivityModel.getSize() == null) {
                    mModel.sizeInsert();
                } else {
                    mModel.update();
                }
                goListFragment();
            }
        });
    }


    private void goButtonClick() {
        mBinding.goButton.setOnClickListener(v -> {
            Uri link = Uri.parse(String.valueOf(mBinding.link.getText()));
            if (!String.valueOf(link).startsWith(getString(R.string.http))) {
                link = Uri.parse(getString(R.string.http) + link);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
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
            else mModel.getImageUri().setValue(data.getData());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivityModel.setFolder(null);
    }

    private void cropImage(Uri data) {
        Intent intent = mModel.getCropIntent(data);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private void goListFragment() {
        mOnBackPressedCallBack.remove();
        requireActivity().onBackPressed();
    }

    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        if (mActivityModel.getSize() == null) menu.getItem(1).setVisible(false);
        if (mModel.getSize().isFavorite()) menu.getItem(0).setIcon(R.drawable.icon_favorite);
        else menu.getItem(0).setIcon(R.drawable.icon_favorite_border);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater
            inflater) {
        inflater.inflate(R.menu.input_output_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.input_output_delete) {
            showDialog(new DeleteConFirmDialog());
            return true;
        } else if (item.getItemId() == R.id.input_output_favorite) {
            mModel.getSize().setFavorite(!mModel.getSize().isFavorite());
            if (mModel.getSize().isFavorite()) item.setIcon(R.drawable.icon_favorite);
            else item.setIcon(R.drawable.icon_favorite_border);
            return true;
        } else {
            if (mActivityModel.getSize() == null) setOnBackPressedCallBackInput();
            else setOnBackPressedCallBackOutput();
            return true;
        }
    }

    //dialog click----------------------------------------------------------------------------------
    private void showDialog(DialogFragment dialog) {
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), null);
    }

    @Override
    public void goBackConfirmClick() {
        goListFragment();
    }

    @Override
    public void deleteConfirmClick() {
        mModel.delete();
        goListFragment();
    }

    @Override
    public void imageClearConfirmClick() {
        mModel.getImageUri().setValue(null);
        mBinding.addIcon.setVisibility(View.VISIBLE);
    }
    //----------------------------------------------------------------------------------------------
}