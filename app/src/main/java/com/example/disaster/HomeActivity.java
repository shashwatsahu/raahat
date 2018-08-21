package com.example.disaster;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disaster.account.AccountManager;
import com.example.disaster.basemaps.BasemapsDialogFragment;
import com.example.disaster.util.MapsActivity2;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;

public class HomeActivity extends Fragment implements LoaderManager.LoaderCallbacks<List<String>>{

    private String TAG = "HomeActivity";

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

        //todo URL

        Button helpMeBtn = rootView.findViewById(R.id.help_me_btn);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        Button surveyBtn = rootView.findViewById(R.id.survey_btn);

        surveyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        helpMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        final ImageButton playButton= rootView.findViewById(R.id.alarm_btn);
        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity().getApplicationContext(),"Play",Toast.LENGTH_SHORT).show();
                if(!flag_sound) {
                    mediaPlayer.start();
                    Log.i(TAG, "Activated:" + playButton.isEnabled());
                    flag_sound = true;
                } else{
                    mediaPlayer.stop();
                    flag_sound = false;
                }
            }
        });

        final ImageButton flash = rootView.findViewById(R.id.flash_btn);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toggleFlashLight();
            }
        });

        final ImageButton mapBtn = rootView.findViewById(R.id.map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity().getApplicationContext(), MapsActivity2.class));
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

        final ImageButton droneBtn = rootView.findViewById(R.id.drone_btn);
        droneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasemapsDialogFragment basemapsDialogFragment = new BasemapsDialogFragment();
                basemapsDialogFragment.show(getActivity().getFragmentManager(), "BaseMap");
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

    private void checkSettings() {
        // Is GPS enabled?
        boolean gpsEnabled = locationTrackingEnabled();
        // Is there internet connectivity?
        boolean internetConnected = internetConnectivity();

        if (gpsEnabled && internetConnected) {
            setView();
        }else if (!gpsEnabled) {
            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            showDialog(gpsIntent, REQUEST_LOCATION_SETTINGS, getString(R.string.location_tracking_off));
        }else if(!internetConnected)	{
            Intent internetIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            showDialog(internetIntent, REQUEST_WIFI_SETTINGS, getString(R.string.wireless_off));
        }
    }

    private boolean locationTrackingEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean internetConnectivity(){
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getActiveNetworkInfo();
        if (wifi == null){
            return false;
        }else {
            return wifi.isConnected();
        }
    }

    private void showDialog(final Intent intent, final int requestCode, String message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity().getApplicationContext());
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getString(R.string.open_location_options), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
                startActivityForResult(intent, requestCode);
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create().show();
    }

    private void setView() {
        if (AccountManager.getInstance().isSignedIn()) {
            // we are signed in to a portal - show the content browser to choose
            // a map
            showContentBrowser();
        } else {
            // show the default map
            showMap(null, null);
        }
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
    public void showMap(String portalItemId, String basemapPortalItemId) {
        // remove existing MapFragment explicitly, simply replacing it can cause
        // the app to freeze when switching basemaps
        FragmentTransaction transaction;
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.Fragment currentMapFragment = fragmentManager.findFragmentByTag(MapFragment.TAG);
        if (currentMapFragment != null) {
            transaction = fragmentManager.beginTransaction();
            transaction.remove(currentMapFragment);
            transaction.commit();
        }

        Log.i(TAG, "portal:" + portalItemId + " basemap:" + basemapPortalItemId);

        MapFragment mapFragment = MapFragment.newInstance(portalItemId, basemapPortalItemId);

        transaction = fragmentManager.beginTransaction();
        Log.i(TAG, "show map");
       //todo
        // transaction.replace(R.id.maps_app_activity_content_frame, mapFragment, MapFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();

        getActivity().invalidateOptionsMenu(); // reload the options menu
    }

    /**
     * opens a default map.
     */

    /**
     * Opens the content browser that shows the user's maps.
     */
    private void showContentBrowser() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.Fragment browseFragment = fragmentManager.findFragmentByTag(ContentBrowserFragment.TAG);
        if (browseFragment == null) {
            browseFragment = new ContentBrowserFragment();
        }

        if (!browseFragment.isVisible()) {
            Log.i(TAG, "in the showcontentbrowser()");

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //transaction.add(R.id.maps_app_activity_content_frame, browseFragment, ContentBrowserFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();

            getActivity().invalidateOptionsMenu(); // reload the options menu
        }

        //todo mDrawerLayout.closeDrawers();
    }

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

                    Toast.makeText(getActivity().getApplicationContext(), address + " " + city + " " + state + " " + country
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

    /**
     * Callback received when a permissions request has been completed.
     */
}
