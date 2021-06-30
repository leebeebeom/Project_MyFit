package com.leebeebeom.closetnote.util.auth;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.leebeebeom.closetnote.databinding.ActivityMainBinding;
import com.leebeebeom.closetnote.util.ToastUtil;

import javax.inject.Inject;

import static com.leebeebeom.closetnote.ui.MainActivity.TAG;

public class Verification extends BaseAuth {

    private final FirebaseAuth mAuth;
    private final ActionCodeSettings mActionCodeSettings;

    @Inject
    public Verification(Context context, ActivityMainBinding activityBinding, ToastUtil toastUtil,
                        FirebaseAuth auth, ActionCodeSettings actionCodeSettings) {
        super(context, activityBinding, toastUtil);
        mAuth = auth;
        mActionCodeSettings = actionCodeSettings;
    }

    public void sendEmailVerification(SignUp.EmailSignUpListener listener) {
        if (mAuth.getCurrentUser() != null)
            mAuth.getCurrentUser().sendEmailVerification(mActionCodeSettings).
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            listener.emailSignUpSuccess();
                        else {
                            mToastUtil.showSendVerificationMailFail();
                            Log.d(TAG, "SignUpFragment : sendEmailVerification: " + task.getException());
                        }
                        hideIndicator();
                    });
    }

    public void reSendVerificationEmail() {
        if (mAuth.getCurrentUser() != null) {
            showIndicator();
            mAuth.getCurrentUser().sendEmailVerification(mActionCodeSettings)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            mToastUtil.showEmailReSent();
                        else if (task.getException() instanceof FirebaseTooManyRequestsException)
                            mToastUtil.showTooManyRequest();
                        else {
                            mToastUtil.showUnknownError();
                            Log.d(TAG, "ReVerificationFragment : reSend: " + task.getException());
                        }
                    });
            hideIndicator();
        }
    }

    public void sendPasswordResetEmail(String email, PasswordResetEmailListener listener) {
        if (isConnect()) {
            showIndicator();
            mAuth.sendPasswordResetEmail(email, mActionCodeSettings)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            listener.sendPasswordResetEmailSuccess();
                        else listener.sendPasswordResetEmailFail(task.getException());
                        hideIndicator();
                    });
        }
    }

    public interface PasswordResetEmailListener {
        void sendPasswordResetEmailSuccess();

        void sendPasswordResetEmailFail(Exception e);
    }
}
