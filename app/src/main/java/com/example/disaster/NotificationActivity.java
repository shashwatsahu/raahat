package com.example.disaster;

import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class NotificationActivity extends Fragment {

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private GestureDetectorCompat gestureDetectorCompat;
    private SparseBooleanArray selectedItems;
    private static final String TAG = "Notification";
    private NoticationAdapter myAdapter;
    ArrayList<String> arrayList;
    private Button submit;

    public NotificationActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_notification, container, false);
        arrayList = new ArrayList<>();

        arrayList.add("Mild Tremors Felt in areas of Satna MP [345 km from your Location ");
        arrayList.add("Your Friend Shantanu Mishra was marked safe in the Earthquake");
        arrayList.add("Your Friend Sankalp Sahu was marked safe in the Earthquake");
        arrayList.add("Govt. announces 2cr for the Relief Work");
        arrayList.add("Make your locality and Home Disaster Proof. Talk to an expert or request a demo");

        myAdapter = new NoticationAdapter(getActivity().getApplicationContext(), arrayList);

        recyclerView = rootView.findViewById(R.id.notification_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(myAdapter);

        return rootView;
    }
}
