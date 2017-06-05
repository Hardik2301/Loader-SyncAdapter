package com.example.imac.loaderexample.localDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by imac on 5/23/17.
 */

public class SqliteDB extends SQLiteOpenHelper {

    public static final String DB_Name="Sample";
    public static final String Table_Name="Test";
    public static final String TB_Id="id";
    public static final String TB_Name="name";
    public static final String TB_City="city";
    public static final String TB_Country="country";
    public static final String TB_Mno="mobile";

    private static final int DB_Version=1;


    public SqliteDB(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String str="CREATE TABLE "+Table_Name+"("+TB_Id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                        TB_Name+" TEXT,"+TB_City+" TEXT,"+TB_Country+" TEXT,"+TB_Mno+" INTEGER)";
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
        onCreate(db);
    }
}
