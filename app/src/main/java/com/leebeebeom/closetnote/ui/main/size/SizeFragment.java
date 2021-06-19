package com.leebeebeom.closetnote.ui.main.size;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.leebeebeom.closetnote.NavGraphSizeArgs;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.databinding.FragmentSizeBinding;
import com.leebeebeom.closetnote.ui.BaseFragment;
import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.OnTextChange;
import com.leebeebeom.closetnote.util.adapter.AutoCompleteAdapter;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.experimental.Accessors;

import static android.app.Activity.RESULT_OK;
import static com.leebeebeom.closetnote.ui.dialog.DeleteImageDialog.DELETE_IMAGE;

@AndroidEntryPoint
@Accessors(prefix = "m")
public class SizeFragment extends BaseFragment {
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String TYPE_IMAGE = "image/*";
    private final Intent mGalleryIntent = new Intent(Intent.ACTION_PICK).setType(TYPE_IMAGE);
    @Inject
    NavController mNavController;
    @Inject
    ActivityMainBinding mActivityBinding;
    @Inject
    @Getter
    AutoCompleteAdapter mAutoCompleteAdapter;
    private SizeViewModel mModel;
    private FragmentSizeBinding mBinding;
    private long mSizeId;
    private ActivityResultLauncher<Intent> mSetImageLauncher;
    private ActivityResultLauncher<Intent> mGetImageLauncher;
    private Uri mUri;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if ((isInput() && mModel.isInputContentsChanged()) || (mSizeId != -1 && mModel.isOutputContentsChanged()))
                    CommonUtil.navigate(mNavController, R.id.sizeFragment, SizeFragmentDirections.toGoBack());
                else mNavController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(SizeViewModel.class);
        mSizeId = NavGraphSizeArgs.fromBundle(getArguments()).getSizeId();
        long parentId = NavGraphSizeArgs.fromBundle(getArguments()).getParentId();
        int parentIndex = NavGraphSizeArgs.fromBundle(getArguments()).getParentIndex();
        mModel.setSizeLive(parentId, mSizeId, parentIndex);

        mSetImageLauncher = getSetImageLauncher();
        mGetImageLauncher = getGetImageLauncher();

        setHasOptionsMenu(true);
    }

    @NotNull
    private ActivityResultLauncher<Intent> getSetImageLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null)
                mModel.getImageUriLive().setValue(result.getData().getData());
        });
    }

    @NotNull
    private ActivityResultLauncher<Intent> getGetImageLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null)
                //from gallery
                cropImage(result.getData().getData());
            else if (result.getData() == null)
                //from camera
                cropImage(mUri);
        });
    }

    private void cropImage(Uri data) {
        Intent intent = mModel.getCropIntent(data);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            mSetImageLauncher.launch(intent);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        addBottomAppBar();
        hideCustomTitle();
        hideSearchBar();
        fabChange(R.drawable.icon_save);

        mBinding = FragmentSizeBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        mBinding.setModel(mModel);
        mBinding.setLifecycleOwner(this);

        addBrandNameTextChangedListener();
        setFabClickListener();
        return mBinding.getRoot();
    }

    private void addBrandNameTextChangedListener() {
        mBinding.etBrand.addTextChangedListener(new OnTextChange() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.layoutBrand.setErrorEnabled(false);
            }
        });

        mBinding.etName.addTextChangedListener(new OnTextChange() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) mBinding.layoutName.setErrorEnabled(false);
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
                if (isInput()) mModel.insertSize();
                else mModel.updateSize();
                mNavController.popBackStack();
            }
        });
    }

    private boolean isInput() {
        return mSizeId == -1;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeDialogLive();
    }

    @Override
    public LockableScrollView getScrollView() {
        return mBinding.sv;
    }

    private void observeDialogLive() {
        getMainGraphViewModel().getBackStackEntryLive().observe(getViewLifecycleOwner(), navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(DELETE_IMAGE).observe(navBackStackEntry, o -> {
                    //TODO 캐시 파일이든 뭐든 URI로 삭제
                    mModel.getImageUriLive().setValue(null);
                }));
    }

    @Override
    public void onDestroy() {
        mBinding = null;
        super.onDestroy();
    }

    public void getImage() {
        mUri = mModel.getUri();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);

        Intent chooser = Intent.createChooser(mGalleryIntent, getString(R.string.size_fragment_attach_picture));

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        if (chooser.resolveActivity(requireActivity().getPackageManager()) != null)
            mGetImageLauncher.launch(mGalleryIntent);
    }

    public boolean navigateDeleteImage() {
        if (mModel.getImageUriLive().getValue() != null)
            CommonUtil.navigate(mNavController, R.id.sizeFragment, SizeFragmentDirections.toDeleteImage());
        return true;
    }

    public void openBrowser() {
        String link = String.valueOf(mBinding.etLink.getText());
        if (!TextUtils.isEmpty(link)) {
            if (!link.startsWith(HTTPS) && !link.startsWith(HTTP))
                link = HTTPS + link;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivity(intent);
        }
    }

    //menu------------------------------------------------------------------------------------------
    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        mModel.getSizeLive().observe(getViewLifecycleOwner(), size -> {
            menu.getItem(0).setIcon(size.isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_favorite_border);
            menu.getItem(0).setOnMenuItemClickListener(item -> {
                size.setFavorite(!size.isFavorite());
                mModel.getSizeLive().setValue(size);
                return true;
            });
        });
        menu.getItem(1).setVisible(mSizeId != 0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_size, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_size_delete) {
            CommonUtil.navigate(mNavController, R.id.sizeFragment,
                    SizeFragmentDirections.toDeleteSize(mSizeId));
            return true;
        }
        return false;
    }
}