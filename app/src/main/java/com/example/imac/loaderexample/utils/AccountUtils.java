package com.example.imac.loaderexample.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.example.imac.loaderexample.localDB.DBItem;

import java.util.List;

/**
 * Created by imac on 5/25/17.
 */
public final class AccountUtils {

    private AccountUtils() {
    }

    public static boolean isAccountRegistered(Context context, String accountType) {
        return getAccount(context, accountType) != null;
    }

    public static Account getAccount(Context context, String accountName, String accountType) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        for (Account account : accounts) {
            if (account.name.equals(accountName)) {
                return account;
            }
        }
        return null;
    }

    public static Account getAccount(Context context, String accountType) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    public static boolean accountExists(Context context, String accountName, String accountType) {
        return getAccount(context, accountName, accountType) != null;
    }

    public static String getAuthToken(Context context, Account account, String authTokenType) {
        AccountManager accountManager = AccountManager.get(context);
        return accountManager.peekAuthToken(account, authTokenType);
    }

    public static boolean isTokenValid(Context context, Account account, String authTokenType) {
        return getAuthToken(context, account, authTokenType) != null;
    }

    public static void invalidateToken(Context context, Account account, String authTokenType) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.invalidateAuthToken(account.type, getAuthToken(context, account, authTokenType));
    }

    public interface TokenOperation{
        void TokenExpired();
        void dataFetchedFromServer(List<DBItem> mlist);
    }
}
