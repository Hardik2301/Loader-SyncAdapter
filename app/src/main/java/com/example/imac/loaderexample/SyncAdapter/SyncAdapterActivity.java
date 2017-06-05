package com.example.imac.loaderexample.SyncAdapter;

import android.accounts.Account;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SyncRequest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import com.example.imac.loaderexample.SyncAdapter.auth.AuthenticatorService;
import com.example.imac.loaderexample.SyncAdapter.sync.SyncAdapter;
import com.example.imac.loaderexample.adapter.RecyclerviewCursorAdapter;
import com.example.imac.loaderexample.localDB.CustomProvider;
import com.example.imac.loaderexample.R;
import com.example.imac.loaderexample.utils.AccountUtils;

import static android.content.ContentResolver.SYNC_EXTRAS_EXPEDITED;
import static android.content.ContentResolver.SYNC_EXTRAS_MANUAL;

/**
 * Created by imac on 5/25/17.
 */

public class SyncAdapterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int REQUEST_LOGIN = 1;
    public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    RecyclerView mRecyclerview;
    RecyclerviewCursorAdapter mAdapter;
    Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (checkRegistration()) {
            Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();
            configurePeriodicSync(this, SYNC_INTERVAL, SYNC_FLEXTIME);
        }

        mRecyclerview=(RecyclerView)findViewById(R.id.recyclerview);
        mAdapter=new RecyclerviewCursorAdapter(this,mCursor);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account account = AccountUtils.getAccount(getApplicationContext(), AuthenticatorService.ACCOUNT_TYPE);
                String authority = CustomProvider.providerName;
                ContentResolver.setSyncAutomatically(account, authority, true);
            }
        });

        mAdapter.setOnItemClickListener(new RecyclerviewCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

        Account account = AccountUtils.getAccount(getApplicationContext(), AuthenticatorService.ACCOUNT_TYPE);
        String authority = CustomProvider.providerName;
        boolean autSync = ContentResolver.getSyncAutomatically(account, authority);
        Log.e("Is Account Syncable: ", autSync+"");
        //SyncUtils.CreateSyncAccount(this);
        //getLoaderManager().initLoader(1,null,this);
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
            //SyncUtils.TriggerRefresh();
            Account account=AccountUtils.getAccount(this,AuthenticatorService.ACCOUNT_TYPE);
            Bundle b = new Bundle();
            // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
            b.putBoolean(SYNC_EXTRAS_MANUAL, true);
            b.putBoolean(SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(account, CustomProvider.providerName, b);
            return true;
        }else if(id == R.id.action_logout){
            Log.e("onOptionsItemSelected: ", "Clicked");
            AccountUtils.invalidateToken(this, AccountUtils.getAccount(this, AuthenticatorService.ACCOUNT_TYPE),
                    AuthenticatorService.AUTHTOKEN_TYPE_STANDARD);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        mAdapter.swapCursor(c);
        if(c.getCount()== 0){
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.emptyView).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private boolean checkRegistration() {
        Account registeredAccount = AccountUtils.getAccount(this, AuthenticatorService.ACCOUNT_TYPE);
        if (registeredAccount == null || !AccountUtils.isTokenValid(this, registeredAccount, AuthenticatorService.AUTHTOKEN_TYPE_STANDARD)) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            if (registeredAccount != null) {
                loginIntent.putExtra(LoginActivity.ARG_DEFAULT_EMAIL, registeredAccount.name);
            }
            startActivityForResult(loginIntent, REQUEST_LOGIN);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                if (checkRegistration()) {
                    configurePeriodicSync(this, SYNC_INTERVAL, SYNC_FLEXTIME);
                }
            }
        } else {
            finish();
        }
    }

    public void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = AccountUtils.getAccount(this, AuthenticatorService.ACCOUNT_TYPE);
        String authority = CustomProvider.providerName;
        ContentResolver.setIsSyncable(account, authority, 1);
        ContentResolver.setSyncAutomatically(account, authority, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(syncFinishedReceiver, new IntentFilter(SyncAdapter.ACTION_FINISHED_SYNC));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncFinishedReceiver);
    }

    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Brodcast receiver", "Sync finished, should refresh nao!!");
            AccountUtils.invalidateToken(context, AccountUtils.getAccount(context, AuthenticatorService.ACCOUNT_TYPE),
                    AuthenticatorService.AUTHTOKEN_TYPE_STANDARD);
            if (checkRegistration()) {
            }
        }
    };

}
