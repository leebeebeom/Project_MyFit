package com.leebeebeom.closetnote.ui.dialog;

import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.user.UserApiClient;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.util.ToastUtil;
import com.nhn.android.naverlogin.OAuthLogin;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AnonymouslySignOutDialog extends BaseDialog {
    @Inject
    FirebaseAuth mAuth;
    @Inject
    OAuthLogin mOAuthLogin;
    @Inject
    UserApiClient mUserApiClient;
    @Inject
    ToastUtil mToastUtil;

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder.makeConfirmDialog(getString(R.string.anonymously_sing_out))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            mAuth.signOut();
            mOAuthLogin.logout(requireContext());
            mUserApiClient.logout(throwable -> null);
            mNavController.navigate(Uri.parse(getString(R.string.deep_link_sign_in)));
            dismiss();
            mToastUtil.showSignOutSuccess();
        };
    }

    @Override
    protected int getResId() {
        return R.id.anonymouslySignOutDialog;
    }
}
