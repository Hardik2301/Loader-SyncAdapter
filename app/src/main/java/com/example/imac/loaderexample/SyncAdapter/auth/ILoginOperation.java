package com.example.imac.loaderexample.SyncAdapter.auth;

import android.util.Log;

import com.example.imac.loaderexample.utils.SuncAdapterUtils;

import java.util.HashMap;

/**
 * Created by imac on 5/25/17.
 */
public class ILoginOperation implements LoginOpreation {

    @Override
    public String signUp(String email, String password) throws Exception {
        Log.e("signUp: ", "called");
        String URL = "http://hardikpatel.co.nf/API/testLogin.php";
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("email",email);
        map.put("password",password);
        String token= SuncAdapterUtils.authenticateUser(URL,map);
        return token;
    }

    @Override
    public String signIn(String email, String password) throws Exception {
        Log.e("signIn: ", "called");
        String URL = "http://hardikpatel.co.nf/API/testLogin.php";
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("email",email);
        map.put("password",password);
        String token= SuncAdapterUtils.authenticateUser(URL,map);
        return token;
    }

    @Override
    public void logOut() {
        Log.e("Logout :","Clicked");
    }

    @Override
    public boolean isUserAuthenticated() {
        Log.e("isUserAuthenticated :","Clicked");
        return true;
    }
}
