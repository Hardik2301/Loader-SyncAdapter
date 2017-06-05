package com.example.imac.loaderexample.SyncAdapter.auth;

/**
 * Created by imac on 5/25/17.
 */

public interface LoginOpreation {

    String signUp(final String email, final String password) throws Exception;

    String signIn(final String email, final String password) throws Exception;

    boolean isUserAuthenticated();

    void logOut();
}
