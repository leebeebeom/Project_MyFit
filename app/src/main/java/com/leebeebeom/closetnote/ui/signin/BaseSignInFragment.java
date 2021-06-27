package com.leebeebeom.closetnote.ui.signin;

import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BaseSignInFragment extends Fragment {
    @Inject
    protected ActivityMainBinding mActivityBinding;
    @Inject
    protected NavController mNavController;

    protected void addBottomAppBar() {
        if (mActivityBinding.root.findViewById(R.id.bottomAppBar) == null) {
            mActivityBinding.root.addView(mActivityBinding.fab);
            mActivityBinding.root.addView(mActivityBinding.fabTop);
            mActivityBinding.root.addView(mActivityBinding.bottomAppBar);
        }
    }

    protected void removeBottomAppBar() {
        if (mActivityBinding.root.findViewById(R.id.bottomAppBar) != null) {
            mActivityBinding.root.removeView(mActivityBinding.fab);
            mActivityBinding.root.removeView(mActivityBinding.fabTop);
            mActivityBinding.root.removeView(mActivityBinding.bottomAppBar);
        }
    }

    protected void addAppBar() {
        if (mActivityBinding.root.findViewById(R.id.actionBar) == null)
            mActivityBinding.root.addView(mActivityBinding.actionBar.getRoot());
    }

    protected void removeAppBar() {
        if (mActivityBinding.root.findViewById(R.id.actionBar) != null)
            mActivityBinding.root.removeView(mActivityBinding.actionBar.getRoot());
    }

    protected void showIndicator() {
        mActivityBinding.indicator.show();
    }

    protected void hideIndicator() {
        mActivityBinding.indicator.hide();
    }

    protected void openBrowser(String domain) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(domain));
        if (intent.resolveActivity(requireContext().getPackageManager()) != null)
            startActivity(intent);
    }
}
