package com.example.disaster;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.dd.CircularProgressButton;

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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "submit btn");
                if(myAdapter.getSelectedItems().size() > 0){
                    for (int i = 0; i < myAdapter.getSelectedItems().size(); i++){
                        Product product = myAdapter.getSelectedItems().get(i);
                        Log.i(TAG, "name:" + product.getProductName() + " order:" + product.getOrders());
                    }
                }
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

    private void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

    private void simulateErrorProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
                if (value == 99) {
                    button.setProgress(-1);
                }
            }
        });
        widthAnimation.start();
    }

}
