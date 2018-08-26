package com.example.disaster;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by hp on 28-01-2018.
 */

@SuppressLint("Registered")
public class QueryUtils extends Application {

    private static final String TAG = "QUERYUTILS";

   /*todo private static List<ProductDetails> demoModel;
    private static SparseArray<ProductDetails> demoMap;

    public static String category; */
    private QueryUtils(){}

    public static List<String> extractEarthquakes(String jsonResponse) {

        List<String> list = new ArrayList<>();
        list.add("no");

        if (TextUtils.isEmpty(jsonResponse)) {
            Log.e(TAG, "JSON IS EMPTY!");
            return null;
        }

        /*todo initialize arraylist objects
        demoModel = new ArrayList<>();
        demoMap = new SparseArray<>();*/

        JSONObject jsonObject;

        // todo ArrayList<ProductDetails> productDetails = new ArrayList<>();

        try {
            jsonObject = new JSONObject(jsonResponse);
            //todo return the values

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

        public static final void removeItemFromList(int position) {
        try {
            Log.i(TAG, "position:" + position);
            } catch (IndexOutOfBoundsException e){
            Log.i(TAG, "Index out of bounds:"+e+ "Position :"+position);
        }
    }

    public static URL createUrl(Bundle bundle){
        URL url = null;

       /*todo category = bundle.getString(KeyValue.URL_KEY_CATEGORY);
        Log.i(TAG, "category:" + category);

        String stringUrl = bundle.getString(KeyValue.URL_KEY);*/

       //todo url should be there... change it
        String stringUrl = bundle.getString(KeyValue.URL_KEY);

        Log.i(TAG, "url:" + stringUrl);
        try {
            url = new URL(stringUrl);
            } catch (MalformedURLException e){
            Log.e("QUERY", "error in url:"+e);
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException{

        String jsonResponse = "";

        if(url == null){
        Log.e("URL ", "NULL");
        return jsonResponse;
       }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{

            Log.i(TAG, "http:");
              urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();

            Log.i(TAG, "Status:" + urlConnection.getResponseCode());

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

                Log.i(TAG, "status:" + 200);
            }
        } catch (IOException e){
            Log.e(TAG, "InputReader error: " +e.getMessage());
        }
        finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        Log.i(TAG, "Read from stream:" + output.toString());
       return output.toString();
    }
}