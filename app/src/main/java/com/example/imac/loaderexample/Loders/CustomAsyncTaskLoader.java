package com.example.imac.loaderexample.Loders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.imac.loaderexample.localDB.DBItem;
import com.example.imac.loaderexample.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imac on 5/24/17.
 */

public class CustomAsyncTaskLoader extends AsyncTaskLoader<List<DBItem>> {

    List<DBItem> mList;

    public CustomAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<DBItem> loadInBackground() {
        mList=new ArrayList<DBItem>();
        Log.e("loadInBackground: ", "Loading Started");
        final String URL = "http://jsonplaceholder.typicode.com/users";

        /*JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray> () {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    VolleyLog.e("Response:%n %s", response.toString(4));

                    for(int i=0;i<response.length();i++){
                        JSONObject obj=response.getJSONObject(i);

                        DBItem item=new DBItem();
                        item.setId(obj.getInt("id"));
                        item.setName(obj.getString("name"));

                        JSONObject addObj=obj.getJSONObject("address");

                        item.setCity(addObj.getString("street"));
                        item.setCountry(addObj.getString("city"));
                        item.setMobileNo(989898989);

                        Log.e("loadInBackground: item", item.getName());
                        mList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        // add the request object to the queue to be executed
        ApplicationController.getInstance().addToRequestQueue(req);*/

        mList= Utils.fetchApiData(URL);
        return mList;
    }

    @Override
    public void deliverResult(List<DBItem> data) {
        super.deliverResult(data);
    }
}
