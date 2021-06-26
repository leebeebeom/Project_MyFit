package com.leebeebeom.closetnote.di;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.user.UserApiClient;
import com.leebeebeom.closetnote.BuildConfig;
import com.leebeebeom.closetnote.R;
import com.nhn.android.naverlogin.OAuthLogin;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AuthModule {
    @Singleton
    @Provides
    public static FirebaseAuth provideFirebaseAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
        return auth;
    }

    @Singleton
    @Provides
    public static ActionCodeSettings provideActionCodeSettings(@ApplicationContext Context context) {
        return ActionCodeSettings.newBuilder()
                .setUrl(context.getString(R.string.firebase_dynamic_link))
                .setAndroidPackageName(BuildConfig.APPLICATION_ID, false, null)
                .build();
    }

    @Singleton
    @Provides
    public static OAuthLogin provideOAuthLogin(@ApplicationContext Context context) {
        OAuthLogin authLogin = OAuthLogin.getInstance();
        authLogin.init(context, context.getString(R.string.naver_client_id), context.getString(R.string.naver_client_secret), context.getString(R.string.app_name));
        return authLogin;
    }

    @Qualifiers.GoogleSignInIntent
    @Singleton
    @Provides
    public static Intent provideGoogleSignInIntent(@ApplicationContext Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(context, gso);
        return client.getSignInIntent();
    }

    @Singleton
    @Provides
    public static UserApiClient provideUserApiClient(){
        return UserApiClient.getInstance();
    }
}
