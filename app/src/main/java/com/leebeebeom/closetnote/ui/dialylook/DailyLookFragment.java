package com.leebeebeom.closetnote.ui.dialylook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.leebeebeom.closetnote.R;

public class
DailyLookFragment extends Fragment {

    private DailyLookViewModel mViewModel;

    public static DailyLookFragment newInstance() {
        return new DailyLookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DailyLookViewModel.class);
    }

    public String getString() {
        return "a";
    }

}