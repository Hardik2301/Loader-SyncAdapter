package com.example.imac.loaderexample.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.imac.loaderexample.localDB.DBItem;
import com.example.imac.loaderexample.utils.AccountUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class SuncAdapterUtils {

    static AccountUtils.TokenOperation mOperation;
    private SuncAdapterUtils() {

    }

    public static String authenticateUser(String URL, HashMap<String,String> map) {
        URL url = createUrl(URL);
        Log.e("fetchApiData: API:-", URL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url,map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String token = extracttokenFromJson(jsonResponse);

        return token;
    }

    public static List<DBItem> getUserlist(String URL, HashMap<String,String> map,AccountUtils.TokenOperation mOpration1) {
        mOperation = mOpration1;
        URL url = createUrl(URL);
        Log.e("fetchApiData: API:-", URL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url,map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<DBItem> mList = extractusersFromJson(jsonResponse,mOpration1);

        return mList;
    }

    public static Map<String,DBItem> getUserlist1(String URL, HashMap<String,String> map,AccountUtils.TokenOperation mOpration1) {
        mOperation = mOpration1;
        URL url = createUrl(URL);
        Log.e("fetchApiData: API:-", URL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url,map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String,DBItem> mList = extractusersFromJson1(jsonResponse,mOpration1);

        return mList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url, HashMap<String, String> postDataParams) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("Error response code: " , urlConnection.getResponseCode()+"");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String extracttokenFromJson(String JSONStr) {

        if (TextUtils.isEmpty(JSONStr)) {
            return null;
        }
        String token=null;

        try {
            JSONObject baseJsonResponse = new JSONObject(JSONStr);
            boolean Isaccess=baseJsonResponse.getBoolean("success");

            if(Isaccess){

                JSONArray jarray=baseJsonResponse.getJSONArray("details");
                JSONObject jsonObject=jarray.getJSONObject(0);

                token=jsonObject.getString("token");
                Log.e("extracttokenFromJson: ", "Login Successfull"+" token:"+token);
            }else{
                token = null;
                Log.e("extracttokenFromJson: ", "Login fail");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private static List<DBItem> extractusersFromJson(String JSONStr, AccountUtils.TokenOperation mOpration1) {

        if (TextUtils.isEmpty(JSONStr)) {
            return null;
        }
        ArrayList<DBItem> tList = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(JSONStr);
            boolean Isaccess=baseJsonResponse.getBoolean("success");

            if(Isaccess) {
                JSONArray jsonArray = baseJsonResponse.getJSONArray("details");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    DBItem item = new DBItem();
                    item.setId(Integer.parseInt(obj.getString("id")));
                    item.setName(obj.getString("name"));
                    item.setCity(obj.getString("city"));
                    item.setCountry(obj.getString("country"));
                    String mno = obj.getString("mobile");
                    item.setMobileNo(Integer.parseInt(mno.substring(0,8)));
                    //Log.e("loadInBackground: item", item.getName());
                    tList.add(item);
                }
                mOpration1.dataFetchedFromServer(tList);
            }else{
                Log.e("extractusersFromJson: ", baseJsonResponse.getString("message"));
                mOpration1.TokenExpired();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tList;
    }

    private static Map<String,DBItem> extractusersFromJson1(String JSONStr, AccountUtils.TokenOperation mOpration1) {

        if (TextUtils.isEmpty(JSONStr)) {
            return null;
        }
        Map<String,DBItem> tList = new HashMap<String, DBItem>();

        try {
            JSONObject baseJsonResponse = new JSONObject(JSONStr);
            boolean Isaccess=baseJsonResponse.getBoolean("success");

            if(Isaccess) {
                JSONArray jsonArray = baseJsonResponse.getJSONArray("details");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    DBItem item = new DBItem();
                    item.setId(Integer.parseInt(obj.getString("id")));
                    item.setName(obj.getString("name"));
                    item.setCity(obj.getString("city"));
                    item.setCountry(obj.getString("country"));
                    String mno = obj.getString("mobile");
                    item.setMobileNo(Integer.parseInt(mno.substring(0,8)));
                    //Log.e("loadInBackground: item", item.getName());
                    tList.put(item.getId()+"",item);
                }
            }else{
                Log.e("extractusersFromJson: ", baseJsonResponse.getString("message"));
                mOpration1.TokenExpired();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tList;
    }


}
