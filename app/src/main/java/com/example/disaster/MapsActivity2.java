package com.example.disaster;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disaster.KeyValue;
import com.example.disaster.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, ItemAdapter.ItemListener {

    private static final String TAG = "Mapsactivity2";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String ADDRESS_REQUEST_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";

    private MarkerOptions markerOptions;
    private CameraPosition cameraPosition;
    private SupportMapFragment mapFragment;
    private String[] strings;
    private Location location;
    private boolean mAddressRequested;

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private ItemAdapter mAdapter;
    boolean flag = false;
    BottomSheetBehavior behavior;
    BottomSheetBehavior behaviorAdd;

    private Marker marker;

    private String address, state, city, country;

    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private TextView mLocationAddressTextView;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int RC_SIGN_IN = 123;

    private GoogleMap mMap;
    double lat, lng;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        final Dialog situationDialog = new Dialog(this);
        situationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        situationDialog.setCancelable(false);
        situationDialog.setContentView(R.layout.safe_dialog);

        View bottomSheet1 = findViewById(R.id.bottom_sheet);

        behavior = BottomSheetBehavior.from(bottomSheet1);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_map2);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       /* CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
        View addBottomSheet = coordinatorLayout.findViewById(R.id.add_products_co);
        behaviorAdd = BottomSheetBehavior.from(addBottomSheet);*/

        ImageButton addBtn = findViewById(R.id.add_products);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(MapsActivity2.this, DroneActivity.class));
            }
        });

        ArrayList<String> items = new ArrayList<>();
        items.add("Rescue operator");
        items.add("Medical camps");

        mAdapter = new ItemAdapter(items, this);
        recyclerView.setAdapter(mAdapter);

        ImageButton bottomSheetBtn =  findViewById(R.id.bottom_list);

        bottomSheetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!flag) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    flag = true;
                }
                else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    flag = false;
                }

            }
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                Log.i(TAG, " on state changed");
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        Button denyBtn = situationDialog.findViewById(R.id.deny_safe_btn);
        denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marker.setTag( KeyValue.NOTSAFE);
                situationDialog.dismiss();
            }
        });

        Button confirmBtn = situationDialog.findViewById(R.id.confirm_safe_bnt);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker.setTag(KeyValue.SAFE);
                situationDialog.dismiss();
            }
        });

        situationDialog.show();

        double[] latLng = getIntent().getDoubleArrayExtra(KeyValue.LATLNG);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(latLng != null) {
            lat = latLng[1];
            lng = latLng[0];
            Log.i(TAG, "size:" + latLng.length);
        }

        ImageButton floatingActionButton = findViewById(R.id.current_location_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateValuesFromBundle(savedInstanceState);

                mAddressRequested = false;
                mAddressOutput = "";

                // Create and launch sign-in intent

                // Set up the login form.

                if (location != null) {
                    startIntentService();
                    Log.i(TAG, "location not null");
                    return;
                }
                else
                    Log.i(TAG, "Location is null");

                mAddressRequested = true;
            }
        });

         // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place1: " + place.getName());
                LatLng latLng = place.getLatLng();

                lat = latLng.latitude;
                lng = latLng.longitude;

                cameraPosition = new CameraPosition.Builder()
                        .target(latLng)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                marker.setPosition(latLng);
                Log.i(TAG, "lat:" + latLng.latitude + " long:" + latLng.longitude);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else
            getAddress();

    }

    private boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(shouldProvideRationale){
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

          /*  showSnackbar(R.string.permission_rationale, android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
                }
            });*/
        } else{
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private void getAddress() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "not granted",Toast.LENGTH_SHORT).show();
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location mlocation) {
                if (mlocation == null) {
                    Log.i(TAG, "onSuccess:null");
                    return;
                }

                location = mlocation;

                if(!Geocoder.isPresent()){
                   // showSnackbar(getString(R.string.no_geocoder_available));
                    return;
                }

                startIntentService();

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "getLastLocation:onFailure",e);
            }
        });
    }

    private void startIntentService() {
        Log.i(TAG, "startIntentService");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SYDNEY = new LatLng(-33.88,151.21);
        LatLng CURRENTLATLNG = new LatLng(lat, lng);

 // Obtain the map from a MapFragment or MapView.

// Move the camera instantly to Sydney with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));

// Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

// Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 10000, null);

// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
         cameraPosition = new CameraPosition.Builder()
                .target(CURRENTLATLNG)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        markerOptions = new MarkerOptions()
                .position(CURRENTLATLNG)
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .anchor(0.0f, 1.0f)
                .title("Your position :\n ")
                .snippet(lat + " and " + lng);

        marker = mMap.addMarker(markerOptions);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "updateValues");
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(ADDRESS_REQUEST_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUEST_KEY);
            }
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void displayAddressOutput(){

        //address.setText(mAddressOutput);
         Log.i(TAG, "1String:" + mAddressOutput);
    }

    @Override
    public void onItemClick(String item) {

        switch (item){
            case "Rescue operator":
                flag = false;
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;

            case "Medical camps":
                flag = false;
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }

    }

    private class AddressResultReceiver extends android.os.ResultReceiver {

        AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData){
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            displayAddressOutput();

            if(resultCode == Constants.SUCCESS_RESULT){
                showLog(getString(R.string.address_found));

                strings = resultData.getStringArray(Constants.LIST_OF_DATA);
                if(strings != null) {
                    //todo  Log.i(TAG, "Lat" + strings[1] + " Lon: " + strings[2]);
                    address = strings[0];
                    state = strings[6];
                    country = (strings[7]);
                    city = (strings[8]);

                    lat = Double.parseDouble(strings[2]);
                    lng = Double.parseDouble(strings[1]);

                    //changing the location...
                    LatLng latLng1 = new LatLng(lat, lng);

                    cameraPosition = new CameraPosition.Builder()
                            .target(latLng1)      // Sets the center of the map to Mountain View
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    //mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    //.position(latLng1));
                    marker.setPosition(latLng1);
                    Log.i(TAG, "lat:" + latLng1.latitude + " long:" + latLng1.longitude);

                    Toast.makeText(getApplicationContext(), address + " " + city + " " + state + " " + country
                            , Toast.LENGTH_SHORT).show();
                }
            }

            mAddressRequested = false;
            //updateUIWidgets();

        }
    }

    private void showLog(String text){
        Log.i(TAG, text);
    }

}
