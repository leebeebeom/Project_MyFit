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
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.MainActivity;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ActivityMainBinding;
import com.example.project_myfit.databinding.FragmentSizeBinding;
import com.example.project_myfit.databinding.IncludeBottomBinding;
import com.example.project_myfit.databinding.IncludeEtcBinding;
import com.example.project_myfit.databinding.IncludeTopBinding;
import com.example.project_myfit.dialog.DialogViewModel;
import com.example.project_myfit.search.adapter.AutoCompleteAdapter;
import com.example.project_myfit.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.CROP_REQUEST_CODE;
import static com.example.project_myfit.util.MyFitConstant.DELETE_IMAGE_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.GET_IMAGE_REQUEST_CODE;
import static com.example.project_myfit.util.MyFitConstant.HTTP;
import static com.example.project_myfit.util.MyFitConstant.HTTPS;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class SizeFragment extends Fragment {
    private SizeViewModel mModel;
    private FragmentSizeBinding mBinding;
    private ActivityMainBinding mActivityBinding;
    private IncludeTopBinding mTopBinding;
    private IncludeBottomBinding mBottomBinding;
    private IncludeEtcBinding mEtcBinding;
    private NavController mNavController;
    private long mSizeId;
    private String mParentCategory;
    private boolean mIsSearchView;
    private long mParentId;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mSizeId == 0 && isInputContentsChanged())
                    CommonUtil.navigate(mNavController, R.id.sizeFragment, SizeFragmentDirections.toGoBackDialog(R.id.nav_graph_main));
                else if (mSizeId != 0 && mModel.isOutputContentsChanged()) {
                    if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
                        requireActivity().finish();
                    else
                        CommonUtil.navigate(mNavController, R.id.sizeFragment, SizeFragmentDirections.toGoBackDialog(R.id.nav_graph_main));
                } else mNavController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private boolean isInputContentsChanged() {
        return mModel.getImageUriLive().getValue() != null ||//이미지 추가
                mModel.getSize().isFavorite() ||//favorite 변경
                !TextUtils.isEmpty(mBinding.etBrand.getText()) ||
                !TextUtils.isEmpty(mBinding.etName.getText()) ||
                !TextUtils.isEmpty(mBinding.etSize.getText()) ||
                !TextUtils.isEmpty(mBinding.etLink.getText()) ||
                !TextUtils.isEmpty(mBinding.etMemo.getText()) ||
                !TextUtils.isEmpty(mTopBinding.etLength.getText()) ||
                !TextUtils.isEmpty(mTopBinding.etShoulder.getText()) ||
                !TextUtils.isEmpty(mTopBinding.etChest.getText()) ||
                !TextUtils.isEmpty(mTopBinding.etSleeve.getText()) ||
                !TextUtils.isEmpty(mBottomBinding.etLength.getText()) ||
                !TextUtils.isEmpty(mBottomBinding.etWaist.getText()) ||
                !TextUtils.isEmpty(mBottomBinding.etThigh.getText()) ||
                !TextUtils.isEmpty(mBottomBinding.etRise.getText()) ||
                !TextUtils.isEmpty(mBottomBinding.etHem.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etOption1.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etOption2.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etOption3.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etOption4.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etOption5.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.etOption6.getText());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentId = SizeFragmentArgs.fromBundle(getArguments()).getParentId();
        mSizeId = SizeFragmentArgs.fromBundle(getArguments()).getSizeId();
        mParentCategory = SizeFragmentArgs.fromBundle(getArguments()).getParentCategory();
        mIsSearchView = requireActivity().getIntent().getExtras() != null;

        mModel = new ViewModelProvider(this).get(SizeViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mModel.initResources(mParentId, mSizeId, mParentCategory);
        mBinding = FragmentSizeBinding.inflate(inflater);
        if (mSizeId != 0) mBinding.layoutTime.setVisibility(View.VISIBLE);
        mActivityBinding = ((MainActivity) requireActivity()).mBinding;

        mTopBinding = IncludeTopBinding.inflate(inflater);
        mBottomBinding = IncludeBottomBinding.inflate(inflater);
        mEtcBinding = IncludeEtcBinding.inflate(inflater);

        addContentsView();
        setSelection();

        mBinding.setSize(mModel.getSize());
        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER))
            mTopBinding.setSize(mModel.getSize());
        else if (mParentCategory.equals(BOTTOM))
            mBottomBinding.setSize(mModel.getSize());
        else mEtcBinding.setSize(mModel.getSize());

        return mBinding.getRoot();
    }

    private void addContentsView() {
        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER))
            mBinding.container.addView(mTopBinding.getRoot());
        else if (mParentCategory.equals(BOTTOM))
            mBinding.container.addView(mBottomBinding.getRoot());
        else mBinding.container.addView(mEtcBinding.getRoot());
    }

    private void setSelection() {
        mBinding.etBrand.setSelection(mBinding.etBrand.length());
        mBinding.etName.setSelection(mBinding.etName.length());
        mBinding.etSize.setSelection(mBinding.etSize.length());
        mBinding.etLink.setSelection(mBinding.etLink.length());
        mBinding.etMemo.setSelection(mBinding.etMemo.length());

        mTopBinding.etLength.setSelection(mTopBinding.etLength.length());
        mTopBinding.etShoulder.setSelection(mTopBinding.etShoulder.length());
        mTopBinding.etChest.setSelection(mTopBinding.etChest.length());
        mTopBinding.etSleeve.setSelection(mTopBinding.etSleeve.length());

        mBottomBinding.etLength.setSelection(mBottomBinding.etLength.length());
        mBottomBinding.etWaist.setSelection(mBottomBinding.etWaist.length());
        mBottomBinding.etThigh.setSelection(mBottomBinding.etThigh.length());
        mBottomBinding.etRise.setSelection(mBottomBinding.etRise.length());
        mBottomBinding.etHem.setSelection(mBottomBinding.etHem.length());

        mEtcBinding.etOption1.setSelection(mEtcBinding.etOption1.length());
        mEtcBinding.etOption2.setSelection(mEtcBinding.etOption2.length());
        mEtcBinding.etOption3.setSelection(mEtcBinding.etOption3.length());
        mEtcBinding.etOption4.setSelection(mEtcBinding.etOption4.length());
        mEtcBinding.etOption5.setSelection(mEtcBinding.etOption5.length());
        mEtcBinding.etOption6.setSelection(mEtcBinding.etOption6.length());

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityBinding.fabTop.setVisibility(View.GONE);
        setBrandAutoCompleteAdapter();

        observeImageUriLive();
        observeDialogLive();
    }

    private void setBrandAutoCompleteAdapter() {
        HashSet<String> autoCompleteHashSet = new HashSet<>();
        List<String> brandList = mModel.getSizeBrandList();
        for (String s : brandList)
            autoCompleteHashSet.add(s.trim());

        List<String> autoCompleteList = new ArrayList<>(autoCompleteHashSet);
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(requireContext(), R.layout.item_auto_complete_texv_view, R.id.tv_item_ac_tv, autoCompleteList);
        mBinding.etBrand.setAdapter(autoCompleteAdapter);
    }

    private void observeImageUriLive() {
        mModel.getImageUriLive().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) mBinding.iconAddImage.setVisibility(View.GONE);
            else mBinding.iconAddImage.setVisibility(View.VISIBLE);

            mBinding.iv.setImageURI(uri);
        });
    }

    private void observeDialogLive() {
        DialogViewModel dialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.nav_graph_main)).get(DialogViewModel.class);

        dialogViewModel.getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry -> {
            observeImageClear(navBackStackEntry);
            observeDeleted(navBackStackEntry);
            observeGoBack(navBackStackEntry);
        });
    }

    private void observeImageClear(@NotNull androidx.navigation.NavBackStackEntry navBackStackEntry) {
        navBackStackEntry.getSavedStateHandle().getLiveData(DELETE_IMAGE_CONFIRM).observe(navBackStackEntry, o ->
                mModel.getImageUriLive().setValue(null));
    }

    private void observeDeleted(@NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(SIZE_DELETE_CONFIRM).observe(navBackStackEntry, o -> {
//            if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
//                requireActivity().finish();
//            else mNavController.popBackStack(R.id.sizeFragment, true);
//        });
    }

    private void observeGoBack(@NotNull NavBackStackEntry navBackStackEntry) {
//        navBackStackEntry.getSavedStateHandle().getLiveData(GO_BACK_CONFIRM).observe(navBackStackEntry, o -> {
//            if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
//                requireActivity().finish();
//            else mNavController.popBackStack(R.id.sizeFragment, true);
//        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBrandNameTextChangeListener();
        setImageClickListener();
        setImageLongClickListener();
        setGoButtonClickListener();
        setFabClickListener();
    }

    private void setBrandNameTextChangeListener() {
        mBinding.etBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.layoutBrand.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.layoutName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setImageClickListener() {
        mBinding.iv.setOnClickListener(v -> {
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
            else mModel.getImageUriLive().setValue(data.getData());
        }
    }

    private void cropImage(Uri data) {
        Intent intent = mModel.getCropIntent(data);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private void setImageLongClickListener() {
        mBinding.iv.setOnLongClickListener(v -> {
            if (mModel.getImageUriLive().getValue() != null)
                CommonUtil.navigate(mNavController, R.id.sizeFragment,
                        SizeFragmentDirections.toDeleteImageDialog(R.id.nav_graph_main));
            return true;
        });
    }

    private void setGoButtonClickListener() {
        mBinding.btnGo.setOnClickListener(v -> {
            String link = String.valueOf(mBinding.etLink.getText());
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

    private void setFabClickListener() {
        mActivityBinding.fab.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mBinding.etBrand.getText().toString().trim())
                    || TextUtils.isEmpty(String.valueOf(mBinding.etName.getText()).trim())) {
                if (TextUtils.isEmpty(mBinding.etBrand.getText().toString().trim()))
                    mBinding.layoutBrand.setError(getString(R.string.size_brand_necessary_field));
                if (TextUtils.isEmpty(String.valueOf(mBinding.etName.getText()).trim()))
                    mBinding.layoutName.setError(getString(R.string.size_name_necessary_field));
            } else {
                if (mSizeId == 0) mModel.sizeInsert(mIsSearchView);
                else mModel.sizeUpdate();

                if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
                    requireActivity().finish();
                else mNavController.popBackStack();
            }
        });
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
            CommonUtil.navigate(mNavController, R.id.sizeFragment,
                    SizeFragmentDirections.toSizeDeleteDialog(mSizeId, R.id.nav_graph_main));
            return true;
        } else if (item.getItemId() == R.id.menu_size_favorite) {
            mModel.getSize().setFavorite(!mModel.getSize().isFavorite());
            item.setIcon(mModel.getSize().isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
            return true;
        } else if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0) {
            requireActivity().finish();
            return true;
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
    }
}