package com.example.project_myfit.fragment.inputoutput;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentInputOutputBinding;
import com.example.project_myfit.databinding.IncludeBottomBinding;
import com.example.project_myfit.databinding.IncludeEtcBinding;
import com.example.project_myfit.databinding.IncludeTopOuterBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.searchActivity.adapter.AutoCompleteAdapter;
import com.example.project_myfit.util.MyFitConstant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.CROP_REQUEST_CODE;
import static com.example.project_myfit.util.MyFitConstant.GET_IMAGE_REQUEST_CODE;
import static com.example.project_myfit.util.MyFitConstant.IMAGE_CLEAR_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class InputOutputFragment extends Fragment {
    private InputOutputViewModel mModel;
    private FragmentInputOutputBinding mBinding;
    private IncludeTopOuterBinding mTopOuterBinding;
    private IncludeBottomBinding mBottomBinding;
    private IncludeEtcBinding mEtcBinding;
    private NavController mNavController;
    private long mSizeId;
    private String mParentCategory;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mSizeId == 0) inputOnBackPressed();
                else outputOnBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void inputOnBackPressed() {
        if (isDefaultInputChanged() || isSizeInputChanged())
            mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToGoBackDialog());
        else mNavController.popBackStack();
    }

    private boolean isDefaultInputChanged() {
        return mModel.getMutableImageUri().getValue() != null ||//이미지 추가
                mModel.getSize().isFavorite() ||//favorite 변경
                !TextUtils.isEmpty(mBinding.brand.getText()) ||
                !TextUtils.isEmpty(mBinding.name.getText()) ||
                !TextUtils.isEmpty(mBinding.size.getText()) ||
                !TextUtils.isEmpty(mBinding.link.getText()) ||
                !TextUtils.isEmpty(mBinding.memo.getText());
    }

    private boolean isSizeInputChanged() {
        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER)) {
            return !TextUtils.isEmpty(mTopOuterBinding.length.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.shoulder.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.chest.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.sleeve.getText());
        } else if (mParentCategory.equals(BOTTOM)) {
            return !TextUtils.isEmpty(mBottomBinding.bottomLength.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.waist.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.thigh.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.rise.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.hem.getText());
        } else return !TextUtils.isEmpty(mEtcBinding.option1.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.option2.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.option3.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.option4.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.option5.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.option6.getText());
    }

    private void outputOnBackPressed() {
        if (mModel.getCompareResult())
            mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToGoBackDialog());
        else mNavController.popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long categoryId = InputOutputFragmentArgs.fromBundle(getArguments()).getCategoryId();
        long colderId = InputOutputFragmentArgs.fromBundle(getArguments()).getFolderId();
        mSizeId = InputOutputFragmentArgs.fromBundle(getArguments()).getSizeId();
        mParentCategory = InputOutputFragmentArgs.fromBundle(getArguments()).getParentCategory();

        mModel = new ViewModelProvider(this).get(InputOutputViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        mModel.initResources(categoryId, colderId, mSizeId, mParentCategory);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentInputOutputBinding.inflate(inflater);
        View view = mBinding.getRoot();
        setLayout(inflater);
        setSelection();
        setData();
        return view;
    }

    private void setLayout(LayoutInflater inflater) {
        mTopOuterBinding = IncludeTopOuterBinding.inflate(inflater);
        mBottomBinding = IncludeBottomBinding.inflate(inflater);
        mEtcBinding = IncludeEtcBinding.inflate(inflater);

        if (mParentCategory.equals(MyFitConstant.TOP) || mParentCategory.equals(MyFitConstant.OUTER))
            mBinding.detailFrameLayout.addView(mTopOuterBinding.getRoot());
        else if (mParentCategory.equals(MyFitConstant.BOTTOM))
            mBinding.detailFrameLayout.addView(mBottomBinding.getRoot());
        else mBinding.detailFrameLayout.addView(mEtcBinding.getRoot());

        if (mSizeId != 0) mBinding.timeLayout.setVisibility(View.VISIBLE);
    }

    private void setSelection() {
        mBinding.brand.setSelection(mBinding.brand.length());
        mBinding.name.setSelection(mBinding.name.length());
        mBinding.size.setSelection(mBinding.size.length());
        mBinding.link.setSelection(mBinding.link.length());
        mBinding.memo.setSelection(mBinding.memo.length());

        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER)) {
            mTopOuterBinding.length.setSelection(mTopOuterBinding.length.length());
            mTopOuterBinding.shoulder.setSelection(mTopOuterBinding.shoulder.length());
            mTopOuterBinding.chest.setSelection(mTopOuterBinding.chest.length());
            mTopOuterBinding.sleeve.setSelection(mTopOuterBinding.sleeve.length());
        } else if (mParentCategory.equals(BOTTOM)) {
            mBottomBinding.bottomLength.setSelection(mBottomBinding.bottomLength.length());
            mBottomBinding.waist.setSelection(mBottomBinding.waist.length());
            mBottomBinding.thigh.setSelection(mBottomBinding.thigh.length());
            mBottomBinding.rise.setSelection(mBottomBinding.rise.length());
            mBottomBinding.hem.setSelection(mBottomBinding.hem.length());
        } else {
            mEtcBinding.option1.setSelection(mEtcBinding.option1.length());
            mEtcBinding.option2.setSelection(mEtcBinding.option2.length());
            mEtcBinding.option3.setSelection(mEtcBinding.option3.length());
            mEtcBinding.option4.setSelection(mEtcBinding.option4.length());
            mEtcBinding.option5.setSelection(mEtcBinding.option5.length());
            mEtcBinding.option6.setSelection(mEtcBinding.option6.length());
        }
    }

    private void setData() {
        mBinding.setSize(mModel.getSize());

        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER))
            mTopOuterBinding.setSize(mModel.getSize());
        else if (mParentCategory.equals(BOTTOM))
            mBottomBinding.setSize(mModel.getSize());
        else mEtcBinding.setSize(mModel.getSize());
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBrandAutoCompleteList();

        setImageUriLive();
        setDialogLive();
    }

    private void setBrandAutoCompleteList() {
        List<String> autoCompleteList = new ArrayList<>();
        List<String> brandList = mModel.getSizeBrandList();
        for (String s : brandList)
            if (!autoCompleteList.contains(s)) autoCompleteList.add(s);
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(requireContext(), R.layout.item_auto_complete, R.id.auto_complete_text, autoCompleteList);
        mBinding.brand.setAdapter(autoCompleteAdapter);
    }

    private void setImageUriLive() {
        mModel.getMutableImageUri().observe(getViewLifecycleOwner(), uri -> {
            mBinding.image.setImageURI(uri);
            if (uri != null) mBinding.addIcon.setVisibility(View.GONE);
            else mBinding.addIcon.setVisibility(View.VISIBLE);
        });
    }

    private void setDialogLive() {
        DialogViewModel dialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.main_nav_graph))
                .get(DialogViewModel.class);

        //image clear confirm click
        dialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(IMAGE_CLEAR_CONFIRM_CLICK).observe(navBackStackEntry, o ->
                        mModel.getMutableImageUri().setValue(null)));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        brandNameTextChangeListener();
        imageClick();
        imageLongClick();
        goButtonClick();
        fabClick();
    }

    private void brandNameTextChangeListener() {
        mBinding.brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.brandLayout.setErrorEnabled(false);
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
                if (!TextUtils.isEmpty(s)) mBinding.nameLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void imageClick() {
        mBinding.image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivityForResult(intent, GET_IMAGE_REQUEST_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            //get image result
            if (requestCode == GET_IMAGE_REQUEST_CODE) cropImage(data.getData());
                //cropImage result
            else mModel.getMutableImageUri().setValue(data.getData());
        }
    }

    private void cropImage(Uri data) {
        Intent intent = mModel.getCropIntent(data);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private void imageLongClick() {
        mBinding.image.setOnLongClickListener(v -> {
            if (mModel.getMutableImageUri().getValue() != null)
                mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToImageClearDialog());
            return true;
        });
    }

    private void goButtonClick() {
        mBinding.goButton.setOnClickListener(v -> {
            String link = String.valueOf(mBinding.link.getText());
            if (!TextUtils.isEmpty(link)) {
                if (!link.startsWith(getString(R.string.https)) && !link.startsWith(getString(R.string.http)))
                    link = getString(R.string.https) + link;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    intent.setData(Uri.parse(link));
                    startActivity(intent);
                }
            }
        });
    }

    private void fabClick() {
        requireActivity().findViewById(R.id.activity_fab).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mBinding.brand.getText().toString().trim()) || TextUtils.isEmpty(String.valueOf(mBinding.name.getText()).trim())) {
                if (TextUtils.isEmpty(mBinding.brand.getText().toString().trim()))
                    mBinding.brandLayout.setError(getString(R.string.necessary_field_brand));
                if (TextUtils.isEmpty(String.valueOf(mBinding.name.getText()).trim()))
                    mBinding.nameLayout.setError(getString(R.string.necessary_field_name));
            } else {
                if (mSizeId == 0) mModel.sizeInsert();
                else mModel.update();
                mNavController.popBackStack();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }


    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        menu.getItem(0).setIcon(mModel.getSize().isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
        menu.getItem(1).setVisible(mSizeId != 0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater
            inflater) {
        inflater.inflate(R.menu.input_output_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.input_output_delete) {
            mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToDeleteConfirmDialog(mSizeId));
            return true;
        } else if (item.getItemId() == R.id.input_output_favorite) {
            mModel.getSize().setFavorite(!mModel.getSize().isFavorite());
            item.setIcon(mModel.getSize().isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
            return true;
        }
        return false;
    }
}