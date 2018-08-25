package com.example.disaster;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DroneActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener, View.OnClickListener{

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private GestureDetectorCompat gestureDetectorCompat;
    private SparseBooleanArray selectedItems;
    private static final String TAG = "DRONEACTIVITY";
    private ProductRecyclerAdapter myAdapter;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone);
        ArrayList<Product> productArrayList = new ArrayList<Product>();

        productArrayList.add(new Product("Food Packets", R.drawable.food_packet, 0));
        productArrayList.add(new Product("Mineral Water", R.drawable.water_bottle, 0));
        productArrayList.add(new Product("Medicines", R.drawable.medical_tablets, 0));
        productArrayList.add(new Product("Milk", R.drawable.milk, 0));

        myAdapter = new ProductRecyclerAdapter(this, productArrayList);

        recyclerView = findViewById(R.id.product_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        recyclerView.addOnItemTouchListener(this);
        gestureDetectorCompat = new GestureDetectorCompat(getApplicationContext(), new RecyclerViewDemoOnGestureListener());

        submit = findViewById(R.id.submit_btn);
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.i(TAG, "submit btn");

                  //todo  HTTPAsyncTask httpAsyncTask = new HTTPAsyncTask();
                  //todo httpAsyncTask.doInBackground();

                  //  HttpPost("http://18.210.151.76/requirements");
            }
        });
    }

    private void myToggleSelection(int idx){

       myAdapter.toggleSelection(idx);

        //todo String title = getString(R.string.selected_counts, getSelectedItemCount());
        //  actionMode.setTitle(title);
        Log.i(TAG, "myToggleSelection:" + idx);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.submit_btn:
                    Log.i(TAG, "submit btn");
                    if(myAdapter.getSelectedItems().size() > 0){
                        for (int i = 0; i < myAdapter.getSelectedItems().size(); i++){
                            Product product = myAdapter.getSelectedItems().get(i);
                            Log.i(TAG, "name:" + product.getProductName() + " order:" + product.getOrders());
                        }
                    }
                break;
            }
    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

            int position;

            int idx = -1;

            if (view != null) {

                idx = recyclerView.getChildAdapterPosition(view);
                int idL = recyclerView.getChildLayoutPosition(view);
                myToggleSelection(idx);

                int selectedOrders = myAdapter.getSelectedItems().size();

                Log.i(TAG, "Selected:" + selectedOrders);
                if(selectedOrders > 0){
                    submit.setVisibility(View.VISIBLE);
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }else{
                    submit.setVisibility(View.INVISIBLE);
                }

            }

            if(myAdapter.getSelectedItemCount() > 0){
                submit.setVisibility(View.VISIBLE);
            }else
                submit.setVisibility(View.GONE);

            return super.onSingleTapConfirmed(e);
        }

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            Log.i(TAG, "connected!" + networkInfo.getTypeName());
            // change background color to red

        } else {
            // show "Not Connected"
            // change background color to green
            Log.i(TAG, "disconnect!" + networkInfo.getTypeName());
        }

        return isConnected;
    }


    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    urls[0] = "http://18.210.151.76/requirements";
                    return HttpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
           Log.i(TAG, "post execute:" + result);
        }
    }

    private String HttpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        return conn.getResponseMessage()+"";

    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("name", "shashwat");
        jsonObject.accumulate("country",  "india");

        return jsonObject;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

}