package com.example.disaster;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.disaster.QueryUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by hp on 28-01-2018.
 */

public class BookLoader extends AsyncTaskLoader<List<String>> {

    private static final String  TAG = "BookLoader";
    private Bundle bundle;
    private int id;

    public BookLoader(Context context, Bundle bundle, int id){
        super(context);
        this.id = id;
        this.bundle = new Bundle(bundle);
    }

    @Override
    protected void onStartLoading(){
        Log.i(TAG, "TEST: onStartLoading() called...");
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        List<String> result = null;

        URL url = QueryUtils.createUrl(bundle);

        if(url == null){
            Log.i(TAG, "Url is null1");
            return null;
        }
        Log.i(TAG,"URL:"+id+"-"+url.toString());
        try {
            String line = QueryUtils.makeHttpRequest(url);
            result = QueryUtils.extractEarthquakes(line);

            Log.i(TAG, "Data:" + line);

            if(result == null){
                Log.i(TAG, "Null values in list");
            }

        } catch (IOException e) {
            Log.i(TAG, "io error:"+e);
        }
        return result;
    }
}
