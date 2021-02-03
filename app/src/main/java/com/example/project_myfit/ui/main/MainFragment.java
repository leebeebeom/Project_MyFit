package com.example.project_myfit.ui.main;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentMainBinding;
import com.example.project_myfit.databinding.MainPopupMenuBinding;
import com.example.project_myfit.dialog.AddCategoryDialog;
import com.example.project_myfit.ui.main.adapter.MainDragCallBack;
import com.example.project_myfit.ui.main.database.Category;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//TODO 뷰페이저 2 구현
public class MainFragment extends Fragment implements AddCategoryDialog.AddCategoryConfirmClick {
    public static final String TOP = "TOP";
    public static final String BOTTOM = "BOTTOM";
    public static final String OUTER = "OUTER";
    public static final String ETC = "ETC";
    private MainViewModel mModel;
    private FragmentMainBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private String checkedCategory;
    private LiveData<List<Category>> mLiveData;
    private PopupWindow mPopupWindow;
    private MainPopupMenuBinding mPopupMenuBinding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(MainViewModel.class);
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater);

        mPopupMenuBinding = MainPopupMenuBinding.inflate(inflater);
        mPopupWindow = new PopupWindow(mPopupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Get Category
        getCategory();
        //Toggle View Setting
        ColorStateList textOriginColor = mBinding.btnEtc.getTextColors();
        setToggleGroup(textOriginColor);
        if (checkedCategory == null) {
            mBinding.btnTop.setChecked(true);
        }
        //Click Listener
        setClickListener();
    }

    //Initialize
    private void init() {
        mBinding.recyclerView.setHasFixedSize(true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new MainDragCallBack(mModel.getAdapter()));
        touchHelper.attachToRecyclerView(mBinding.recyclerView);
    }

    //Get Category
    private void getCategory() {
        mLiveData = mModel.getAllChild(checkedCategory);
        mLiveData.observe(getViewLifecycleOwner(), categoryList -> {
            mModel.setLargestOrder();
            mAdapter.setItem(categoryList, mModel);
            mBinding.recyclerView.setAdapter(mAdapter);
            mAdapter.updateDiffUtils(categoryList);
        });
    }

    //Toggle Group Setting
    private void setToggleGroup(ColorStateList textOriginColor) {
        mBinding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            TypedValue typedValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
            int colorControl = typedValue.data;
            requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;
            if (checkedId == mBinding.btnTop.getId()) {
                checkedCategory = TOP;

                mBinding.btnTop.setBackgroundColor(colorControl);
                mBinding.btnTop.setTextColor(colorPrimary);

                mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnBottom.setTextColor(textOriginColor);
                mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnOuter.setTextColor(textOriginColor);
                mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnEtc.setTextColor(textOriginColor);
            } else if (checkedId == mBinding.btnBottom.getId()) {
                checkedCategory = BOTTOM;

                mBinding.btnBottom.setBackgroundColor(colorControl);
                mBinding.btnBottom.setTextColor(colorPrimary);

                mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnTop.setTextColor(textOriginColor);
                mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnOuter.setTextColor(textOriginColor);
                mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnEtc.setTextColor(textOriginColor);
            } else if (checkedId == mBinding.btnOuter.getId()) {
                checkedCategory = OUTER;

                mBinding.btnOuter.setBackgroundColor(colorControl);
                mBinding.btnOuter.setTextColor(colorPrimary);

                mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnTop.setTextColor(textOriginColor);
                mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnBottom.setTextColor(textOriginColor);
                mBinding.btnEtc.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnEtc.setTextColor(textOriginColor);
            } else if (checkedId == mBinding.btnEtc.getId()) {
                checkedCategory = ETC;

                mBinding.btnEtc.setBackgroundColor(colorControl);
                mBinding.btnEtc.setTextColor(colorPrimary);

                mBinding.btnTop.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnTop.setTextColor(textOriginColor);
                mBinding.btnBottom.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnBottom.setTextColor(textOriginColor);
                mBinding.btnOuter.setBackgroundColor(Color.TRANSPARENT);
                mBinding.btnOuter.setTextColor(textOriginColor);

            }
            mRefresh = true;
            if (mLiveData.hasObservers()) {
                mLiveData.removeObservers(getViewLifecycleOwner());
            }
            getCategory();
        });
    }

    //Click Listener
    private void setClickListener() {
        mPopupMenuBinding.addFolder.setOnClickListener(v -> showDialog(new AddCategoryDialog(), "add category"));

        mAdapter.setOnCategoryClickListener(new CategoryAdapter1.OnCategoryClickListener() {
            @Override
            public void onItemClick(Category category) {
                mActivityModel.setCategory(category);
                Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_listFragment);
            }

            @Override
            public void onEditClick(Category category, int position) {
            }

            @Override
            public void onDeleteClick(Category category) {
            }
        });
    }

    private void showDialog(DialogFragment dialog, String tag) {
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), tag);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_popup) {
            mPopupWindow.showAsDropDown(requireActivity().findViewById(R.id.menu_list_popup));
            return true;
        }
        return false;
    }

    @Override
    public void addCategoryConfirmClick(String categoryName) {

    }
}