package com.example.imac.loaderexample.Loders;

import android.app.LoaderManager;
import android.content.Loader;
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
import com.example.imac.loaderexample.localDB.DBItem;

import java.util.ArrayList;
import java.util.List;

public class AsyncLoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<DBItem>> {

    RecyclerView mRecyclerview;
    List<DBItem> mList;
    RecyclerviewAdapter mAdapter;
    private final int Loader_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

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
                getLoaderManager().initLoader(Loader_id,null,AsyncLoaderActivity.this);
            }
        });

        mAdapter.setOnItemClickListener(new RecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

        getLoaderManager().initLoader(Loader_id,null,this);
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
            getLoaderManager().initLoader(Loader_id+1,null,AsyncLoaderActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<DBItem>> onCreateLoader(int id, Bundle args) {
        CustomAsyncTaskLoader loader=new CustomAsyncTaskLoader(getApplicationContext());
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<DBItem>> loader, List<DBItem> data) {
        Log.e("onLoadFinished: ", data.size()+"");
        mList=data;
        mAdapter.UpdateList(mList);
        /*if(mList.size() == 0){
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.emptyView).setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onLoaderReset(Loader<List<DBItem>> loader) {
        //getLoaderManager().restartLoader(Loader_id, null, this);
    }
}
