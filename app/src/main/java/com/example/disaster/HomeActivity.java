package com.example.disaster;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import butterknife.BindView;

public class HomeActivity extends Fragment implements LoaderManager.LoaderCallbacks<List<String>>{

    private String TAG = "HomeActivity";

    private double lat, lng;

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private static final int REQUEST_LOCATION_SETTINGS = 1;
    private static final int REQUEST_WIFI_SETTINGS = 2;
    private static final int REQUEST_ARCGIS_CRED = 3;

    private String address, state, city, country;
    private boolean flag_sound = false;
    /**
     * Id to identity READ_CONTACTS permission request.
     */

    private static final int REQUEST_READ_CONTACTS = 0;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String ADDRESS_REQUEST_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";

    private View rootView;
    private String[] strings;

    @BindView(R.id.maps_app_activity_left_drawer)
    ListView mDrawerList;
    private View mLayout;

    private MediaPlayer mediaPlayer;
    private boolean toggle = false;

    //fetch information...
    private Location location;
    private boolean mAddressRequested;

    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private TextView mLocationAddressTextView;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int RC_SIGN_IN = 123;

    //required for loader manager
    private ProgressBar progressBar;
    private Bundle bundle;
    private LoaderManager loaderManager;
    private LinearLayoutManager layoutManager;
    private Bundle onSaveInstanceState;
    private String categories;


    public HomeActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.activity_home, container, false);

        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.floating_btn);

        //bundle data for url
        bundle = new Bundle();

        String BOOKS_URL ="http://18.210.151.76";
        bundle.putString(KeyValue.URL_KEY, BOOKS_URL);

        loaderManager = getActivity().getLoaderManager();
        Log.i(TAG,"Test:calling initLoader()");
        loaderManager.initLoader(1, bundle, this);

        CardView service = rootView.findViewById(R.id.service_cv);
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Request has been send to VizzBee.Inc", Toast.LENGTH_SHORT).show();
            }
        });

        CardView wiki = rootView.findViewById(R.id.wiki_cv);
        wiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), WikipediaPage.class));
            }
        });

        //todo URL

        Button helpMeBtn = rootView.findViewById(R.id.help_me_btn);

        //initialize current location...
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        ImageView survey = rootView.findViewById(R.id.survey_btn);
        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), Survey.class));
            }
        });

        ImageView arcGisBtn = rootView.findViewById(R.id.arcgis_btn);
        arcGisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(getActivity().getApplicationContext(), ArcgisWeb.class));
            }
        });

        ImageView baseBtn = rootView.findViewById(R.id.base_map_btn);
        baseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), BaseMap.class));
            }
        });

        helpMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Your Request has been accepted!", Toast.LENGTH_SHORT).show();

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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(MainActivity.this, ShopsCategory.class);
                //startActivity(intent);
                //  startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));

                Log.i(TAG,"fab");

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "1070"));

                startActivity(intent);
            }
        });


        mediaPlayer= MediaPlayer.create(getActivity().getApplicationContext(), R.raw.siren);
        final ImageView playButton= rootView.findViewById(R.id.alarm_btn);
        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity().getApplicationContext(),"Play",Toast.LENGTH_SHORT).show();
                if(!flag_sound) {
                    Log.i(TAG, "start");
                    mediaPlayer.start();
                    Log.i(TAG, "Activated:" + playButton.isEnabled());
                    flag_sound = true;
                } else{
                    Log.i(TAG, "stop");
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    flag_sound = false;
                }
            }
        });

        final ImageView flash = rootView.findViewById(R.id.flash_btn);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toggleFlashLight();
            }
        });

        final ImageView mapBtn = rootView.findViewById(R.id.map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(), MapsActivity2.class);
                Log.i(
                        TAG, "Lat:" + lat + " Lng:" + lng
                );
                intent.putExtra(KeyValue.LATLNG, new double[] {lat, lng});

                startActivity(intent);
                /*BasemapsDialogFragment basemapsFrag = new BasemapsDialogFragment();
                basemapsFrag.setBasemapsDialogListener(new BasemapsDialogFragment.BasemapsDialogListener() {

                    @Override
                    public void onBasemapChanged(String itemId) {
                        showMap(null, itemId);
                    }
                });
                basemapsFrag.show(getActivity().getFragmentManager(), null);*/
               // MapFragment mapFragment = new MapFragment();

            }
        });

        final ImageView droneBtn = rootView.findViewById(R.id.drone_btn);
        droneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(), DroneActivity.class));
            }
        });

        return rootView;
    }

    public void toggleFlashLight(){
        toggle=!toggle;
        try {
            Log.i(TAG, " torch");
            CameraManager cameraManager = (CameraManager) getActivity().getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String id : cameraManager.getCameraIdList()) {
                    Log.i(TAG, " torch is 1");
                    // Turn on the flash if camera has one
                    if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Log.i(TAG, " torch is on");
                            cameraManager.setTorchMode(id, true);
                        }
                    }
                }
            }
        } catch (Exception e2) {
            Toast.makeText(getActivity().getApplicationContext(), "Torch Failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Provide an additional rationale to the user if the permission was
            // not granted
            // and the user would benefit from additional context for the use of
            // the permission.
            // Display a SnackBar with a button to request the missing
            // permission.
            Snackbar.make(mLayout, "Location access is required to display the map.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_LOCATION);
                        }
                    }).show();

        } else {
            // Request the permission. The result will be received in
            // onRequestPermissionResult().
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    private boolean locationTrackingEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Opens the map represented by the specified portal item or if null, opens
     * a default map.
     *
     * @param portalItemId
     *            - String representing a portal resource
     * @param basemapPortalItemId
     *            - String representing a basemap portal item
     */

    /**
     * opens a default map.
     */

    /**
     * Opens the content browser that shows the user's maps.
     */

    //getting current location...

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

    private void displayAddressOutput(){

        //address.setText(mAddressOutput);
       //todo Log.i(TAG, "1String:" + mAddressOutput);
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
        int permissionState = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(shouldProvideRationale){
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
                }
            });
        } else{
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void startIntentService() {
        Log.i(TAG, "startIntentService");
        Intent intent = new Intent(getActivity().getApplicationContext(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        getActivity().startService(intent);
    }

    private void getAddress() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getActivity().getApplicationContext(), "not granted",Toast.LENGTH_SHORT).show();
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location mlocation) {
                if (mlocation == null) {
                    Log.i(TAG, "onSuccess:null");
                    return;
                }

                location = mlocation;

                if(!Geocoder.isPresent()){
                    showSnackbar(getString(R.string.no_geocoder_available));
                    return;
                }

                startIntentService();

            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "getLastLocation:onFailure",e);
            }
        });
    }

    private void showSnackbar(final String text){
        /*todo View container = rootView.findViewById(android.R.id.content);
        if(container != null){
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }*/
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener){
     //todo   Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId),Snackbar.LENGTH_INDEFINITE).setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        Log.i(TAG,"onRequestPermissionResult");
        if(requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){
            if(grantResults.length <= 0){
                Log.i(TAG,"User interaction was cancelled.");
            }
            else if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getAddress();
            }
            else{
                showSnackbar(R.string.permission_denied_explanation, R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(getActivity().getApplicationContext(), args, id);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        Log.i(TAG, "ON LOAD FINISHED");

        if( data != null && !data.isEmpty()) {

            Log.i(TAG, "data:" + data.get(0));

        } else {

              Log.i(TAG, "data is null");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
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

                    lat = Double.parseDouble(strings[1]);
                    lng = Double.parseDouble(strings[2]);

                 //   Toast.makeText(getActivity().getApplicationContext(), address + " " + city + " " + state + " " + country
                   // , Toast.LENGTH_SHORT).show();
                }
            }

            mAddressRequested = false;
            //updateUIWidgets();

        }
    }

    private void showLog(String text){
        Log.i(TAG, text);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
}
