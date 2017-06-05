package com.example.imac.loaderexample.localDB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.imac.loaderexample.localDB.SqliteDB;

import java.util.HashMap;

/**
 * Created by imac on 5/23/17.
 */

public class CustomProvider extends ContentProvider {

    public static final String TAG="ContentProvider";
    public static final String providerName="com.imac.loader";
    public static final Uri CONTENT_URI=Uri.parse("content://"+providerName+"/Users");

    public static final int USERS = 1;
    public static final int USER_ID = 2;

    private static HashMap<String, String> USERS_PROJECTION_MAP;

    public static final UriMatcher mUriMatcher;
    static {
        mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(providerName,"Users",USERS);
        mUriMatcher.addURI(providerName,"Users/#",USER_ID);
    }

    private SQLiteDatabase mDB;

    @Override
    public boolean onCreate() {

        SqliteDB mydb=new SqliteDB(getContext());
        mDB=mydb.getWritableDatabase();
        return (mDB == null) ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        queryBuilder.setTables(SqliteDB.Table_Name);
        Log.e(TAG, "query called ");
        switch (mUriMatcher.match(uri)){
            case USERS:
                queryBuilder.setProjectionMap(USERS_PROJECTION_MAP);
                break;
            case USER_ID:
                queryBuilder.appendWhere(SqliteDB.TB_Id +" = "+uri.getPathSegments().get(1));
                break;
            default:
        }

        if(sortOrder == null && sortOrder == ""){
            sortOrder = SqliteDB.TB_Name;
        }

        Cursor c=queryBuilder.query(mDB,projection,selection,selectionArgs,null,null,sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (mUriMatcher.match(uri)){
            case USERS:
                return "vnd.android.cursor.dir/vnd.example.users";
            case USER_ID:
                return "vnd.android.cursor.item/vnd.example.users";
            default:
                throw new IllegalArgumentException("Unsupported uri : "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.e(TAG, "Insert called ");
        long rowid=mDB.insert(SqliteDB.Table_Name,null,values);
        if(rowid > 0){
            Uri mUri= ContentUris.withAppendedId(CONTENT_URI,rowid);
            getContext().getContentResolver().notifyChange(mUri,null);
            return mUri;
        }
        throw new SQLException("Failed to insert record");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.e(TAG, "delete called ");
        int count = 0;
        switch (mUriMatcher.match(uri)){
            case USERS:
                count=mDB.delete(SqliteDB.Table_Name,selection,selectionArgs);
                break;
            case USER_ID:
                String id=uri.getPathSegments().get(1);
                count=mDB.delete(SqliteDB.Table_Name,SqliteDB.TB_Id+" = "+id+(!TextUtils.isEmpty(selection)?" AND ("+selection+')' : ""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri : "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.e(TAG, "Update called ");
        int count = 0;
        switch (mUriMatcher.match(uri)){
            case USERS:
                count=mDB.update(SqliteDB.Table_Name,values,selection,selectionArgs);
                break;
            case USER_ID:
                String id=uri.getPathSegments().get(1);
                count=mDB.update(SqliteDB.Table_Name,values,SqliteDB.TB_Id+" = "+id+(!TextUtils.isEmpty(selection)?" AND ("+selection+')' : ""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri : "+uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
