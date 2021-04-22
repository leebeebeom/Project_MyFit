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
import static com.example.project_myfit.util.MyFitConstant.GO_BACK_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.IMAGE_CLEAR_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.SIZE_DELETE_CONFIRM_CLICK;
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
            mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToGoBackDialog());
        else mNavController.popBackStack();
    }

    private boolean isDefaultInputChanged() {
        return mModel.getMutableImageUri().getValue() != null ||//이미지 추가
                mModel.getSize().isFavorite() ||//favorite 변경
                !TextUtils.isEmpty(mBinding.inputOutputBrandEditText.getText()) ||
                !TextUtils.isEmpty(mBinding.inputOutputNameEditText.getText()) ||
                !TextUtils.isEmpty(mBinding.inputOutputSizeEditText.getText()) ||
                !TextUtils.isEmpty(mBinding.inputOutputLinkEditText.getText()) ||
                !TextUtils.isEmpty(mBinding.inputOutputMemoEditText.getText());
    }

    private boolean isSizeInputChanged() {
        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER)) {
            return !TextUtils.isEmpty(mTopOuterBinding.includeLengthEditText.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.includeShoulderEditText.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.includeChestEditText.getText()) ||
                    !TextUtils.isEmpty(mTopOuterBinding.includeSleeveEditText.getText());
        } else if (mParentCategory.equals(BOTTOM)) {
            return !TextUtils.isEmpty(mBottomBinding.includeBottomLengthEditText.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.includeWaistEditText.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.includeThighEditText.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.includeRiseEditText.getText()) ||
                    !TextUtils.isEmpty(mBottomBinding.includeHemEditText.getText());
        } else return !TextUtils.isEmpty(mEtcBinding.includeOption1EditText.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.includeOption2EditText.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.includeOption3EditText.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.includeOption4EditText.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.includeOption5EditText.getText()) ||
                !TextUtils.isEmpty(mEtcBinding.includeOption6EditText.getText());
    }

    private void outputOnBackPressed() {
        if (mModel.getCompareResult())
            mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToGoBackDialog());
        else if (mIsSearchView && getParentFragmentManager().getBackStackEntryCount() == 0)
            requireActivity().finish();
        else mNavController.popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long parentId = InputOutputFragmentArgs.fromBundle(getArguments()).getParentId();
        mSizeId = InputOutputFragmentArgs.fromBundle(getArguments()).getSizeId();
        mParentCategory = InputOutputFragmentArgs.fromBundle(getArguments()).getParentCategory();
        mIsSearchView = requireActivity().getIntent().getExtras() != null;

        mModel = new ViewModelProvider(this).get(InputOutputViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        mModel.initResources(parentId, mSizeId, mParentCategory);
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
            mBinding.inputOutputDetailContainer.addView(mTopOuterBinding.getRoot());
        else if (mParentCategory.equals(MyFitConstant.BOTTOM))
            mBinding.inputOutputDetailContainer.addView(mBottomBinding.getRoot());
        else mBinding.inputOutputDetailContainer.addView(mEtcBinding.getRoot());

        if (mSizeId != 0) mBinding.inputOutputTimeLayout.setVisibility(View.VISIBLE);
    }

    private void setSelection() {
        mBinding.inputOutputBrandEditText.setSelection(mBinding.inputOutputBrandEditText.length());
        mBinding.inputOutputNameEditText.setSelection(mBinding.inputOutputNameEditText.length());
        mBinding.inputOutputSizeEditText.setSelection(mBinding.inputOutputSizeEditText.length());
        mBinding.inputOutputLinkEditText.setSelection(mBinding.inputOutputLinkEditText.length());
        mBinding.inputOutputMemoEditText.setSelection(mBinding.inputOutputMemoEditText.length());

        if (mParentCategory.equals(TOP) || mParentCategory.equals(OUTER)) {
            mTopOuterBinding.includeLengthEditText.setSelection(mTopOuterBinding.includeLengthEditText.length());
            mTopOuterBinding.includeShoulderEditText.setSelection(mTopOuterBinding.includeShoulderEditText.length());
            mTopOuterBinding.includeChestEditText.setSelection(mTopOuterBinding.includeChestEditText.length());
            mTopOuterBinding.includeSleeveEditText.setSelection(mTopOuterBinding.includeSleeveEditText.length());
        } else if (mParentCategory.equals(BOTTOM)) {
            mBottomBinding.includeBottomLengthEditText.setSelection(mBottomBinding.includeBottomLengthEditText.length());
            mBottomBinding.includeWaistEditText.setSelection(mBottomBinding.includeWaistEditText.length());
            mBottomBinding.includeThighEditText.setSelection(mBottomBinding.includeThighEditText.length());
            mBottomBinding.includeRiseEditText.setSelection(mBottomBinding.includeRiseEditText.length());
            mBottomBinding.includeHemEditText.setSelection(mBottomBinding.includeHemEditText.length());
        } else {
            mEtcBinding.includeOption1EditText.setSelection(mEtcBinding.includeOption1EditText.length());
            mEtcBinding.includeOption2EditText.setSelection(mEtcBinding.includeOption2EditText.length());
            mEtcBinding.includeOption3EditText.setSelection(mEtcBinding.includeOption3EditText.length());
            mEtcBinding.includeOption4EditText.setSelection(mEtcBinding.includeOption4EditText.length());
            mEtcBinding.includeOption5EditText.setSelection(mEtcBinding.includeOption5EditText.length());
            mEtcBinding.includeOption6EditText.setSelection(mEtcBinding.includeOption6EditText.length());
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
            if (!autoCompleteList.contains(s.trim())) autoCompleteList.add(s.trim());
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(requireContext(), R.layout.item_auto_complete, R.id.itemAutoCompleteText, autoCompleteList);
        mBinding.inputOutputBrandEditText.setAdapter(autoCompleteAdapter);
    }

    private void setImageUriLive() {
        mModel.getMutableImageUri().observe(getViewLifecycleOwner(), uri -> {
            mBinding.inputOutputImage.setImageURI(uri);
            if (uri != null) mBinding.inputOutputAddIcon.setVisibility(View.GONE);
            else mBinding.inputOutputAddIcon.setVisibility(View.VISIBLE);
        });
    }

    private void setDialogLive() {
        DialogViewModel dialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(R.id.main_nav_graph))
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
        mBinding.inputOutputBrandEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.inputOutputBrandLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBinding.inputOutputNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.inputOutputNameLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void imageClick() {
        mBinding.inputOutputImage.setOnClickListener(v -> {
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
        mBinding.inputOutputImage.setOnLongClickListener(v -> {
            if (mModel.getMutableImageUri().getValue() != null)
                mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToImageClearDialog());
            return true;
        });
    }

    private void goButtonClick() {
        mBinding.inputOutputGoButton.setOnClickListener(v -> {
            String link = String.valueOf(mBinding.inputOutputLinkEditText.getText());
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
        requireActivity().findViewById(R.id.mainActivityFab).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mBinding.inputOutputBrandEditText.getText().toString().trim())
                    || TextUtils.isEmpty(String.valueOf(mBinding.inputOutputNameEditText.getText()).trim())) {
                if (TextUtils.isEmpty(mBinding.inputOutputBrandEditText.getText().toString().trim()))
                    mBinding.inputOutputBrandLayout.setError(getString(R.string.necessary_field_brand));
                if (TextUtils.isEmpty(String.valueOf(mBinding.inputOutputNameEditText.getText()).trim()))
                    mBinding.inputOutputNameLayout.setError(getString(R.string.necessary_field_name));
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
        inflater.inflate(R.menu.input_output_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menuInputOutputDelete) {
            mNavController.navigate(InputOutputFragmentDirections.actionInputOutputFragmentToDeleteConfirmDialog(mSizeId));
            return true;
        } else if (item.getItemId() == R.id.menuInputOutputFavorite) {
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