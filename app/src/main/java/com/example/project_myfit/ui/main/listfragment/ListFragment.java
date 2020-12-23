package com.example.project_myfit.ui.main.listfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.FragmentListBinding;
import com.example.project_myfit.databinding.TitleListBinding;


public class ListFragment extends Fragment {

    private ListViewModel mModel;
    private FragmentListBinding mBinding;
    private MainActivityViewModel mActivityModel;
    private Animation rotateOpen, rotateClose, fromBottomAdd, toBottomAdd, fromBottomFolder, toBottomAddFolder;
    private boolean isFabOpened = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Binding
        mBinding = FragmentListBinding.inflate(getLayoutInflater());
        TitleListBinding titleBinding = TitleListBinding.inflate(getLayoutInflater());
        //Activity View Model
        mActivityModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        //Set Title
        titleBinding.setChildCategory(mActivityModel.getChildCategory().getChildCategory());
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setCustomView(titleBinding.listTitleContainer);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize
        init();
        //Adapter
        mModel.getSizeList(mActivityModel.getChildCategory().getId()).observe(getViewLifecycleOwner(), Sizes -> {
            mBinding.listFragmentRecyclerView.setAdapter(mModel.setAdapterItem(Sizes));
            mModel.getAdapter().setOnItemClickedListener(size -> {
                //클릭된 size 아이템 MainActivityViewModel에 저장
                mActivityModel.setSize(size);
                //OutputFragment ㄱㄱ
                Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_outputFragment);
                //TODO
                //클릭 리스너
            });
        });
        rotateOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close);
        fromBottomAdd = AnimationUtils.loadAnimation(requireContext(), R.anim.add_from_bottom);
        toBottomAdd = AnimationUtils.loadAnimation(requireContext(), R.anim.add_to_bottom);
        fromBottomFolder = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_from_bottom);
        toBottomAddFolder = AnimationUtils.loadAnimation(requireContext(), R.anim.folder_to_bottom);

        mBinding.fabMain.setOnClickListener(v -> onFabClicked());
        mBinding.fabFolder.setOnClickListener(v -> {
        });
        mBinding.fabAdd.setOnClickListener(v -> {
        });
    }

    private void init() {
        //View Model
        mModel = new ViewModelProvider(this).get(ListViewModel.class);
    }

    private void onFabClicked() {
        setVisibility(isFabOpened);
        setAnimation(isFabOpened);
        setClickable(isFabOpened);
        isFabOpened = !isFabOpened;
    }

    private void setAnimation(boolean isFabOpened) {
        if (!isFabOpened) {
            mBinding.fabAdd.startAnimation(fromBottomAdd);
            mBinding.fabFolder.startAnimation(fromBottomFolder);
            mBinding.fabMain.startAnimation(rotateOpen);
        } else {
            mBinding.fabAdd.startAnimation(toBottomAdd);
            mBinding.fabFolder.startAnimation(toBottomAddFolder);
            mBinding.fabMain.startAnimation(rotateClose);
        }
    }

    private void setVisibility(boolean isFabOpened) {
        if (!isFabOpened) {
            mBinding.fabAdd.setVisibility(View.VISIBLE);
            mBinding.fabFolder.setVisibility(View.VISIBLE);
        } else {
            mBinding.fabAdd.setVisibility(View.INVISIBLE);
            mBinding.fabFolder.setVisibility(View.INVISIBLE);
        }
    }

    private void setClickable(boolean isFabOpened) {
        mBinding.fabAdd.setClickable(!isFabOpened);
        mBinding.fabFolder.setClickable(!isFabOpened);
    }

    private void goInputFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_listFragment_to_inputFragment);
    }
}
//TODO
//item_list_fragment 완성 해야됨
//폴더 추가