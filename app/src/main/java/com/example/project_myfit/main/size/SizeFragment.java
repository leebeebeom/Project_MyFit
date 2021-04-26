package com.example.project_myfit.main.size;

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
import com.example.project_myfit.databinding.FragmentSizeBinding;
import com.example.project_myfit.databinding.IncludeSizeBottomBinding;
import com.example.project_myfit.databinding.IncludeSizeEtcBinding;
import com.example.project_myfit.databinding.IncludeSizeTopOuterBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.search.main.adapter.AutoCompleteAdapter;
import com.example.project_myfit.util.MyFitConstant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.CROP_REQUEST_CODE;
import static com.example.project_myfit.util.MyFitConstant.GET_IMAGE_REQUEST_CODE;
import static com.example.project_myfit.util.MyFitConstant.GO_BACK_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.HTTP;
import static com.example.project_myfit.util.MyFitConstant.HTTPS;
import static com.example.project_myfit.util.MyFitConstant.IMAGE_CLEAR_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.SIZE_DELETE_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class SizeFragment extends Fragment {
    private SizeViewModel mModel;
    private FragmentSizeBinding mBinding;
    private IncludeSizeTopOuterBinding mTopOuterBinding;
    private IncludeSizeBottomBinding mBottomBinding;
    private IncludeSizeEtcBinding mEtcBinding;
    private NavController mNavController;
    private long mSizeId;
    private String mParentCategory;
    private boolean mIsSearchView;

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
            mNavController.navigate(SizeFragmentDirections.actionInputOutputFragmentToGoBackDialog());
        else mNavController.popBackStack();
    }

    private boolean isDefaultInputChanged() {
        return mModel.getMutableImageUri().getValue() != null ||//이미지 추가
                mModel.getSize().isFavorite() ||//favorite 변경
                !TextUtils.isEmpty(mBinding.acTvSizeBrand.getText()) ||
                !TextUtils.isEmpty(mBinding.etSizeName.getText()) ||
                !TextUtils.isEmpty(mBinding.etSizeSize.getText()) ||
                !TextUtils.isEmpty(mBinding.etSizeLink.getText()) ||
                !TextUtils.isEmpty(mBinding.etSizeMemo.getText());
    }

    private boolean isSizeInputChanged() {
        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER)) {
            return !TextUtils.isEmpty(mTopOuterBinding.etIncludeTopOuterLength.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.etIncludeTopOuterShoulder.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.etIncludeTopOuterChest.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.etIncludeTopOuterSleeve.getText());
        } else if (mParentCategory.equals(BOTTOM)) {
            return !TextUtils.isEmpty(mBottomBinding.etIncludeBottomLength.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.etIncludeBottomWaist.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.etIncludeBottomThigh.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.etIncludeBottomRise.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.etIncludeBottomHem.getText());
        } else return !TextUtils.isEmpty(mEtcBinding.etIncludeEtcOption1.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etIncludeEtcOption2.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etIncludeEtcOption3.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etIncludeEtcOption4.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etIncludeEtcOption5.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etIncludeEtcOption6.getText());
    }

    private void outputOnBackPressed() {
        if (mModel.getCompareResult())
            mNavController.navigate(SizeFragmentDirections.actionInputOutputFragmentToGoBackDialog());
        else if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
            requireActivity().finish();
        else mNavController.popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long parentId = SizeFragmentArgs.fromBundle(getArguments()).getParentId();
        mSizeId = SizeFragmentArgs.fromBundle(getArguments()).getSizeId();
        mParentCategory = SizeFragmentArgs.fromBundle(getArguments()).getParentCategory();
        mIsSearchView = requireActivity().getIntent().getExtras() != null;

        mModel = new ViewModelProvider(this).get(SizeViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        mModel.initResources(parentId, mSizeId, mParentCategory);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSizeBinding.inflate(inflater);
        View view = mBinding.getRoot();
        setLayout(inflater);
        setSelection();
        setData();
        return view;
    }

    private void setLayout(LayoutInflater inflater) {
        mTopOuterBinding = IncludeSizeTopOuterBinding.inflate(inflater);
        mBottomBinding = IncludeSizeBottomBinding.inflate(inflater);
        mEtcBinding = IncludeSizeEtcBinding.inflate(inflater);

        if (mParentCategory.equals(MyFitConstant.TOP) || mParentCategory.equals(MyFitConstant.OUTER))
            mBinding.detailContainerSize.addView(mTopOuterBinding.getRoot());
        else if (mParentCategory.equals(MyFitConstant.BOTTOM))
            mBinding.detailContainerSize.addView(mBottomBinding.getRoot());
        else mBinding.detailContainerSize.addView(mEtcBinding.getRoot());

        if (mSizeId != 0) mBinding.timeSizeLayout.setVisibility(View.VISIBLE);
    }

    private void setSelection() {
        mBinding.acTvSizeBrand.setSelection(mBinding.acTvSizeBrand.length());
        mBinding.etSizeName.setSelection(mBinding.etSizeName.length());
        mBinding.etSizeSize.setSelection(mBinding.etSizeSize.length());
        mBinding.etSizeLink.setSelection(mBinding.etSizeLink.length());
        mBinding.etSizeMemo.setSelection(mBinding.etSizeMemo.length());

        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER)) {
            mTopOuterBinding.etIncludeTopOuterLength.setSelection(mTopOuterBinding.etIncludeTopOuterLength.length());
            mTopOuterBinding.etIncludeTopOuterShoulder.setSelection(mTopOuterBinding.etIncludeTopOuterShoulder.length());
            mTopOuterBinding.etIncludeTopOuterChest.setSelection(mTopOuterBinding.etIncludeTopOuterChest.length());
            mTopOuterBinding.etIncludeTopOuterSleeve.setSelection(mTopOuterBinding.etIncludeTopOuterSleeve.length());
        } else if (mParentCategory.equals(BOTTOM)) {
            mBottomBinding.etIncludeBottomLength.setSelection(mBottomBinding.etIncludeBottomLength.length());
            mBottomBinding.etIncludeBottomWaist.setSelection(mBottomBinding.etIncludeBottomWaist.length());
            mBottomBinding.etIncludeBottomThigh.setSelection(mBottomBinding.etIncludeBottomThigh.length());
            mBottomBinding.etIncludeBottomRise.setSelection(mBottomBinding.etIncludeBottomRise.length());
            mBottomBinding.etIncludeBottomHem.setSelection(mBottomBinding.etIncludeBottomHem.length());
        } else {
            mEtcBinding.etIncludeEtcOption1.setSelection(mEtcBinding.etIncludeEtcOption1.length());
            mEtcBinding.etIncludeEtcOption2.setSelection(mEtcBinding.etIncludeEtcOption2.length());
            mEtcBinding.etIncludeEtcOption3.setSelection(mEtcBinding.etIncludeEtcOption3.length());
            mEtcBinding.etIncludeEtcOption4.setSelection(mEtcBinding.etIncludeEtcOption4.length());
            mEtcBinding.etIncludeEtcOption5.setSelection(mEtcBinding.etIncludeEtcOption5.length());
            mEtcBinding.etIncludeEtcOption6.setSelection(mEtcBinding.etIncludeEtcOption6.length());
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
        //TODO check
        requireActivity().findViewById(R.id.fab_main_top).setVisibility(View.GONE);
        setBrandAutoCompleteList();

        setImageUriLive();
        setDialogLive();
    }

    private void setBrandAutoCompleteList() {
        List<String> autoCompleteList = new ArrayList<>();
        List<String> brandList = mModel.getSizeBrandList();
        for (String s : brandList)
            if (!autoCompleteList.contains(s.trim())) autoCompleteList.add(s.trim());
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(requireContext(), R.layout.item_auto_complete_texv_view, R.id.tv_item_ac_tv, autoCompleteList);
        mBinding.acTvSizeBrand.setAdapter(autoCompleteAdapter);
    }

    private void setImageUriLive() {
        mModel.getMutableImageUri().observe(getViewLifecycleOwner(), uri -> {
            mBinding.ivSizePicture.setImageURI(uri);
            if (uri != null) mBinding.iconSizeAddImage.setVisibility(View.GONE);
            else mBinding.iconSizeAddImage.setVisibility(View.VISIBLE);
        });
    }

    private void setDialogLive() {
        DialogViewModel dialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_main))
                .get(DialogViewModel.class);

        //image clear confirm click
        dialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            navBackStackEntry.getSavedStateHandle().getLiveData(IMAGE_CLEAR_CONFIRM_CLICK).observe(navBackStackEntry, o ->
                    mModel.getMutableImageUri().setValue(null));

            navBackStackEntry.getSavedStateHandle().getLiveData(SIZE_DELETE_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
                if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
                    requireActivity().finish();
                else mNavController.popBackStack(R.id.inputOutputFragment, true);
            });

            navBackStackEntry.getSavedStateHandle().getLiveData(GO_BACK_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
                if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
                    requireActivity().finish();
                else mNavController.popBackStack(R.id.inputOutputFragment, true);
            });
        });
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
        mBinding.acTvSizeBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.acTvSizeBrandLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBinding.etSizeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.etSizeNameLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void imageClick() {
        mBinding.ivSizePicture.setOnClickListener(v -> {
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
        mBinding.ivSizePicture.setOnLongClickListener(v -> {
            if (mModel.getMutableImageUri().getValue() != null)
                mNavController.navigate(SizeFragmentDirections.actionInputOutputFragmentToImageClearDialog());
            return true;
        });
    }

    private void goButtonClick() {
        mBinding.btnSizeGo.setOnClickListener(v -> {
            String link = String.valueOf(mBinding.etSizeLink.getText());
            if (!TextUtils.isEmpty(link)) {
                if (!link.startsWith(HTTPS) && !link.startsWith(HTTP))
                    link = HTTPS + link;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    intent.setData(Uri.parse(link));
                    startActivity(intent);
                }
            }
        });
    }

    private void fabClick() {
        requireActivity().findViewById(R.id.fab_main).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mBinding.acTvSizeBrand.getText().toString().trim())
                    || TextUtils.isEmpty(String.valueOf(mBinding.etSizeName.getText()).trim())) {
                if (TextUtils.isEmpty(mBinding.acTvSizeBrand.getText().toString().trim()))
                    mBinding.acTvSizeBrandLayout.setError(getString(R.string.size_brand_necessary_field));
                if (TextUtils.isEmpty(String.valueOf(mBinding.etSizeName.getText()).trim()))
                    mBinding.etSizeNameLayout.setError(getString(R.string.size_name_necessary_field));
            } else {
                if (mSizeId == 0) mModel.sizeInsert(mIsSearchView);
                else mModel.update();

                if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
                    requireActivity().finish();
                else mNavController.popBackStack();
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
        inflater.inflate(R.menu.menu_size, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_size_delete) {
            mNavController.navigate(SizeFragmentDirections.actionInputOutputFragmentToDeleteConfirmDialog(mSizeId));
            return true;
        } else if (item.getItemId() == R.id.menu_size_favorite) {
            mModel.getSize().setFavorite(!mModel.getSize().isFavorite());
            item.setIcon(mModel.getSize().isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
            return true;
        } else if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0) {
            requireActivity().finish();
            return true;
        } else {
            if (mSizeId == 0) inputOnBackPressed();
            else outputOnBackPressed();
            return true;
        }
    }
}