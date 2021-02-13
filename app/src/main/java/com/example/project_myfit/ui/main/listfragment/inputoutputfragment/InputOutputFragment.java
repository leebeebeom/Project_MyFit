package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

import android.app.Activity;
import android.content.Context;
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
import com.example.project_myfit.dialog.GoBackDialog;
import com.example.project_myfit.dialog.ImageClearDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CROP_REQUEST_CODE;
import static com.example.project_myfit.MyFitConstant.GET_IMAGE_REQUEST_CODE;
import static com.example.project_myfit.MyFitConstant.SIZE_ID;

public class InputOutputFragment extends Fragment implements GoBackDialog.GoBackConfirmClick, DeleteConFirmDialog.DeleteConfirmClick, ImageClearDialog.ImageClearConfirmClick {
    private InputOutputViewModel mModel;
    private MainActivityViewModel mActivityModel;
    private FragmentInputOutputBinding mBinding;
    private FloatingActionButton mActivityFab;
    private OnBackPressedCallback mOnBackPressedCallBack;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mOnBackPressedCallBack = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (requireActivity().getIntent().getIntExtra(SIZE_ID, 0) != 0)
                    requireActivity().finish();
                else if (mActivityModel.getSize() == null) onBackPressedInput();
                else onBackPressedOutput();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, mOnBackPressedCallBack);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(InputOutputViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        //check input output
        mModel.checkInputOutput(mActivityModel);

        setHasOptionsMenu(true);
    }

    //onBackPressedCallBack-------------------------------------------------------------------------
    private void onBackPressedInput() {
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
                !TextUtils.isEmpty(mBinding.option6.getText())) showDialog(new GoBackDialog());
        else goListFragment();
    }

    private void onBackPressedOutput() {
        if (mModel.getCompareResult()) showDialog(new GoBackDialog());
        else goListFragment();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentInputOutputBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setLayout();
        setData();
        setSelection();

        mBinding.brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.brandLayout.setError(null);
                    mBinding.brandLayout.setErrorEnabled(false);
                } else {
                    mBinding.brandLayout.setErrorEnabled(true);
                    mBinding.brandLayout.setError(getString(R.string.necessary_field_brand));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBinding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBinding.nameLayout.setError(null);
                    mBinding.nameLayout.setErrorEnabled(false);
                } else {
                    mBinding.nameLayout.setErrorEnabled(true);
                    mBinding.nameLayout.setError(getString(R.string.necessary_field_name));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageClick();
        imageLongClick();
        goButtonClick();

        return view;
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

    private void goButtonClick() {
        mBinding.goButton.setOnClickListener(v -> {
            String link = String.valueOf(mBinding.link.getText());
            if (!link.startsWith(getString(R.string.http))) link = getString(R.string.http) + link;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityFab = requireActivity().findViewById(R.id.activity_fab);
        fabClick();
    }

    private void fabClick() {
        mActivityFab.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mBinding.brand.getText()) || TextUtils.isEmpty(mBinding.name.getText())) {
                if (TextUtils.isEmpty(mBinding.brand.getText())) {
                    mBinding.brandLayout.setErrorEnabled(true);
                    mBinding.brandLayout.setError(getString(R.string.necessary_field_brand));
                }
                if (TextUtils.isEmpty(mBinding.name.getText())) {
                    mBinding.nameLayout.setError(getString(R.string.necessary_field_name));
                    mBinding.nameLayout.setErrorEnabled(true);
                }
            } else {
                if (mActivityModel.getSize() == null) mModel.sizeInsert();
                else mModel.update();
                goListFragment();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mModel.getImageUri().observe(getViewLifecycleOwner(), uri -> {
            mBinding.image.setImageURI(uri);
            if (uri != null) mBinding.addIcon.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

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
        menu.getItem(1).setVisible(!(mActivityModel.getSize() == null));
        menu.getItem(0).setIcon(mModel.getSize().isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
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
            item.setIcon(mModel.getSize().isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
            return true;
        } else {
            if (mActivityModel.getSize() == null) onBackPressedInput();
            else onBackPressedOutput();
            return true;
        }
    }

    //dialog click----------------------------------------------------------------------------------
    private void showDialog(@NotNull DialogFragment dialog) {
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