package com.example.imac.loaderexample.Loders;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.imac.loaderexample.R;
import com.example.imac.loaderexample.adapter.RecyclerviewAdapter;
import com.example.imac.loaderexample.localDB.CustomProvider;
import com.example.imac.loaderexample.localDB.DBItem;
import com.example.imac.loaderexample.localDB.SqliteDB;

import java.util.ArrayList;
import java.util.List;

public class SimpleLoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mRecyclerview;
    List<DBItem> mList;
    RecyclerviewAdapter mAdapter;
    private final int Loader_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        getLoaderManager().initLoader(Loader_id,null,this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mList = new ArrayList<DBItem>();
        mRecyclerview=(RecyclerView)findViewById(R.id.recyclerview);
        mAdapter=new RecyclerviewAdapter(this,mList);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItems();
            }
        });

        mAdapter.setOnItemClickListener(new RecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id == R.id.action_settings){
            Log.e("onOptionsItemSelected: ", "Clicked");
            AddItems();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void AddItems() {

        for(int i=0;i < 10;i++){
            ContentValues cv = new ContentValues();
            cv.put(SqliteDB.TB_Name,"Sample "+i);
            cv.put(SqliteDB.TB_City,"City "+i);
            cv.put(SqliteDB.TB_Country,"Country "+i);
            cv.put(SqliteDB.TB_Mno,"989898989"+i);

            Uri adduri=getContentResolver().insert(CustomProvider.CONTENT_URI,cv);
            Log.e("Insert Uri :",adduri.toString());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String URL = "content://com.imac.loader";
        Uri Users = Uri.parse(URL);
        CursorLoader cl=new CursorLoader(this,Users,null,null,null,null);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        if(c.moveToFirst()){
            do{
                DBItem item=new DBItem();
                item.setId(c.getInt(c.getColumnIndex(SqliteDB.TB_Id)));
                item.setName(c.getString(c.getColumnIndex(SqliteDB.TB_Name)));
                item.setCity(c.getString(c.getColumnIndex(SqliteDB.TB_City)));
                item.setCountry(c.getString(c.getColumnIndex(SqliteDB.TB_Country)));
                item.setMobileNo(c.getInt(c.getColumnIndex(SqliteDB.TB_Mno)));
                mList.add(item);
            }while (c.moveToNext());
        }

        mAdapter.notifyDataSetChanged();
        if(mList.size() == 0){
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.emptyView).setVisibility(View.GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
