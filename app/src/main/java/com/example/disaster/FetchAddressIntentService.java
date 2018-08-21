package com.example.disaster;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hp on 06-01-2018.
 */

public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FetchAddressIS";

    protected ResultReceiver mReceiver;
    public static String name;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *  Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        if(mReceiver == null){
            Log.i(TAG, "No receiver received. There is nowhere to send the results.");
            errorMessage = "ResultReceiver not found";
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        if(location == null){
            errorMessage = getString(R.string.no_location_data_provided);
            Log.i(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }

        List<Address> addresses = null;
        try{

            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        }catch(IOException ioException){
            Toast.makeText(this,"Exception:"+ioException.toString(), Toast.LENGTH_SHORT).show();
        }catch (IllegalArgumentException illegalArgument){
            Toast.makeText(this, "Exception:"+illegalArgument, Toast.LENGTH_SHORT).show();
        }

        if(addresses == null || addresses.size() == 0){
            if(errorMessage.isEmpty()){
                errorMessage = "No address found";
                Log.i(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }else{
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            String [] str = new String[9];
            str[0] = TextUtils.join(System.getProperty("line.separator"),addressFragments);
            str[1] = String.valueOf(address.getLongitude());
            str[2] = String.valueOf(address.getLatitude());
            str[3] = address.getCountryName();
            str[4] = address.getPhone();
            str[5] = address.getCountryName();
            str[6] = address.getAdminArea();
            str[7] = address.getCountryName();
            str[8] = address.getLocality();
           //todo Log.i(TAG, "Lat:" + str[2] + " " + address.getAdminArea() + " " + address.getCountryName() + " address:" + address.getLocality());

           //todo Log.i(TAG, "Lon" + address.getLongitude());
            deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"),addressFragments));
            deliverResultToReceiver(Constants.SUCCESS_RESULT, str);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    private void deliverResultToReceiver(int resultCode, String[] strings){
        Bundle bundle = new Bundle();
        bundle.putStringArray(Constants.LIST_OF_DATA, strings);
        mReceiver.send(resultCode, bundle);
    }

}
