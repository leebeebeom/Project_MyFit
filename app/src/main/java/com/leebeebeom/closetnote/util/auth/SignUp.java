package com.leebeebeom.closetnote.util.auth;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.leebeebeom.closetnote.data.model.model.UserInfo;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import static com.leebeebeom.closetnote.ui.MainActivity.TAG;

public class SignUp extends BaseAuth {

    private final FirebaseAuth mAuth;
    private final Verification mVerification;

    public SignUp(Context context, ActivityMainBinding activityBinding, ToastUtil toastUtil,
                  FirebaseAuth auth, Verification verification) {
        super(context, activityBinding, toastUtil);
        mAuth = auth;
        mVerification = verification;
    }

    public void createUserAnotherEmail(UserInfo userInfo) {
        mAuth.createUserWithEmailAndPassword(userInfo.getEmail(), userInfo.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateUserProfile(userInfo);
                        showAnotherEmailSignInSuccessToast(userInfo);
                    } else {
                        showAnotherEmailSignInFailToast(userInfo);
                        Log.d(TAG, "SignInFragment : createUserAnotherEmail : " + task.getException());
                    }
                    hideIndicatorInThread();
                });
    }

    private void updateUserProfile(UserInfo userInfo) {
        if (mAuth.getCurrentUser() != null)
            mAuth.getCurrentUser().updateProfile(getUserProfileChangeRequest(userInfo))
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            mToastUtil.showProfileUpdateFail();
                            Log.d(TAG, "SignUpFragment : updateUserProfile: " + task.getException());
                        }
                    });
    }

    private UserProfileChangeRequest getUserProfileChangeRequest(UserInfo userInfo) {
        return new UserProfileChangeRequest.Builder()
                .setDisplayName(userInfo.getNickname())
                .setPhotoUri(userInfo.getProfileUrl() != null ? Uri.parse(userInfo.getProfileUrl()) : null)
                .build();
    }

    public void emailSignUp(UserInfo userInfo, EmailSignUpListener listener) {
        if (isConnect()) {
            showIndicator();
            mAuth.createUserWithEmailAndPassword(userInfo.getEmail(), userInfo.getPassword())
                    .addOnCompleteListener(getOnCompleteListener(userInfo, listener));
        }
    }

    public void anonymouslyLinkWithEmail(UserInfo userInfo, SignUp.EmailSignUpListener listener) {
        if (isConnect() && mAuth.getCurrentUser() != null) {
            showIndicator();
            AuthCredential credential = EmailAuthProvider.getCredential(userInfo.getEmail(), userInfo.getPassword());
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(getOnCompleteListener(userInfo, listener));
        }
    }

    @NotNull
    private OnCompleteListener<AuthResult> getOnCompleteListener(UserInfo userInfo, EmailSignUpListener listener) {
        return task -> {
            if (task.isSuccessful()) {
                updateUserProfile(userInfo);
                mVerification.sendEmailVerification(listener);
            } else {
                listener.emailSignUpFail(task.getException());
                hideIndicator();
            }
        };
    }

    public interface EmailSignUpListener {
        void emailSignUpSuccess();

        void emailSignUpFail(Exception e);
    }
}
