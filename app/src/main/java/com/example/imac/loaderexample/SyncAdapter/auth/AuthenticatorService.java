package com.example.imac.loaderexample.SyncAdapter.auth;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by imac on 5/25/17.
 */

public class AuthenticatorService extends Service {

    Authenticator_new mAuthenticator;

    public static final String ACCOUNT_TYPE = "com.imac.example";
    public static final String AUTHTOKEN_TYPE_STANDARD = "Standard";
    public static final String AUTHTOKEN_TYPE_STANDARD_LABEL = "List auth";
    public static final String ACCOUNT_NAME = "demosync";

    public static Account GetAccount() {
        final String accountName = ACCOUNT_NAME;
        return new Account(accountName, ACCOUNT_TYPE);
    }

    @Override
    public void onCreate() {
        Log.e("AuthenticatorService", "onCreate");
        mAuthenticator=new Authenticator_new(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
