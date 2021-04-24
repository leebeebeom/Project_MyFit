package com.example.project_myfit.recycle_bin_activity.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentRecycleBinMainBinding;
import com.example.project_myfit.databinding.PopupMenuRecycleBinMainBinding;

import org.jetbrains.annotations.NotNull;

public class RecycleBinMainFragment extends Fragment {

    private FragmentRecycleBinMainBinding mBinding;
    private PopupMenuRecycleBinMainBinding mPopupMenuBinding;
    private PopupWindow mPopupWindow;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = FragmentRecycleBinMainBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mPopupMenuBinding = PopupMenuRecycleBinMainBinding.inflate(inflater);
        mPopupWindow = new PopupWindow(mPopupMenuBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.trash_main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menuTrashMainSearch) {
            Toast.makeText(requireContext(), "검색 클릭", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.menuTrashMainPopup) {
            mPopupWindow.showAsDropDown(requireActivity().findViewById(R.id.menuTrashMainPopup));
            return true;
        }
        return false;
    }
}