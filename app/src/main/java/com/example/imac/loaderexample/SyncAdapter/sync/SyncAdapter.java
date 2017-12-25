package com.example.imac.loaderexample.SyncAdapter.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.example.imac.loaderexample.SyncAdapter.auth.AuthenticatorService;
import com.example.imac.loaderexample.localDB.CustomProvider;
import com.example.imac.loaderexample.localDB.DBItem;
import com.example.imac.loaderexample.localDB.SqliteDB;
import com.example.imac.loaderexample.utils.AccountUtils;
import com.example.imac.loaderexample.utils.SuncAdapterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by imac on 5/25/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter implements AccountUtils.TokenOperation{

    public static final String ACTION_FINISHED_SYNC = "COM.IMAC.LOADER.ACTION_FINISHED_SYNC";
    ContentResolver mContentResolver;
    private AccountManager mAccountMnager;
    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.e("SyncAdapter", "onCreate");
        mContext=context;
        mContentResolver=context.getContentResolver();
        mAccountMnager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //List<DBItem> mList;
        Map<String,DBItem> mList;

        String authToken = mAccountMnager.peekAuthToken(account, AuthenticatorService.AUTHTOKEN_TYPE_STANDARD);

        if(AccountUtils.isTokenValid(mContext,account,AuthenticatorService.AUTHTOKEN_TYPE_STANDARD)){
            Log.e("SyncAdapter: ", "Token is valid");
        }else{
            Log.e("SyncAdapter: ", "Token is not valid");
        }
        Log.e("SyncAdapter: ", "onPerformSync");
        //String URL = "http://jsonplaceholder.typicode.com/users";
        String URL = "http://hardikpatel.co.nf/API/tempUserlist.php";
        Log.e("SyncAdapter: ", "Token : "+authToken);
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("token",authToken);
        mList= SuncAdapterUtils.getUserlist1(URL,map,this);

        UpdateLocalData(mList);
        /*for (int i=0;i<mList.size();i++){
            DBItem item=mList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(SqliteDB.TB_Id,item.getId());
            cv.put(SqliteDB.TB_Name,item.getName());
            cv.put(SqliteDB.TB_City,item.getCity());
            cv.put(SqliteDB.TB_Country,item.getCountry());
            cv.put(SqliteDB.TB_Mno,item.getMobileNo());

            Uri adduri= null;
            try {
                adduri = provider.insert(CustomProvider.CONTENT_URI,cv);
                //Log.e("Insert Uri :",adduri.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (SQLException e){
                String selection = SqliteDB.TB_Id+"=?";
                String[] selectionArgs = new String[] {cv.getAsString(SqliteDB.TB_Id)};
                try {
                    int ymp = provider.update(CustomProvider.CONTENT_URI, cv, selection, selectionArgs);
                    Log.e("SQLException :", "called");
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }*/
    }

    private void UpdateLocalData(Map<String,DBItem> mList) {

        Cursor c=mContentResolver.query(CustomProvider.CONTENT_URI,null,null,null,null);

        c.moveToFirst();
        DBItem item;
        for(int i=0;i<c.getCount();i++){

            int mLocalid = c.getInt(c.getColumnIndex(SqliteDB.TB_Id));
            item = mList.get(mLocalid+"");
            if(item != null)
            {
                mList.remove(mLocalid+"");
                try {
                    ContentValues cv = new ContentValues();
                    cv.put(SqliteDB.TB_Id,item.getId());
                    cv.put(SqliteDB.TB_Name,item.getName());
                    cv.put(SqliteDB.TB_City,item.getCity());
                    cv.put(SqliteDB.TB_Country,item.getCountry());
                    cv.put(SqliteDB.TB_Mno,item.getMobileNo());

                    String selection = SqliteDB.TB_Id+"=?";
                    String[] selectionArgs = new String[] {cv.getAsString(SqliteDB.TB_Id)};

                    int ymp = mContentResolver.update(CustomProvider.CONTENT_URI, cv, selection, selectionArgs);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }else{
                try{
                    String selection = SqliteDB.TB_Id+"=?";
                    String[] selectionArgs = new String[] {String.valueOf(item.getId())};

                    int ymp = mContentResolver.delete(CustomProvider.CONTENT_URI, selection, selectionArgs);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            c.moveToNext();
        }
        c.close();

        for (DBItem mItem : mList.values()) {
            ContentValues cv = new ContentValues();
            cv.put(SqliteDB.TB_Id,mItem.getId());
            cv.put(SqliteDB.TB_Name,mItem.getName());
            cv.put(SqliteDB.TB_City,mItem.getCity());
            cv.put(SqliteDB.TB_Country,mItem.getCountry());
            cv.put(SqliteDB.TB_Mno,mItem.getMobileNo());

            Uri adduri= null;
            try {
                adduri = mContentResolver.insert(CustomProvider.CONTENT_URI,cv);
                Log.e("Insert Uri :",adduri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void TokenExpired() {
        Intent i = new Intent(ACTION_FINISHED_SYNC);
        mContext.sendBroadcast(i);
        Log.e("Token Expired: ", "Called : ");
    }

    @Override
    public void dataFetchedFromServer(List<DBItem> mlist) {
        Log.e("dataFetchedFromServer: ", "called : ");
    }
}
