package com.example.disaster;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class HomeActivity extends Fragment {

    private String TAG = "HomeActivity";

    private MediaPlayer mediaPlayer;
    private boolean toggle = false;

    public HomeActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);

        mediaPlayer= MediaPlayer.create(getActivity().getApplicationContext(), R.raw.siren);
        final ImageButton playButton= rootView.findViewById(R.id.alarm_btn);
        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getActivity().getApplicationContext(),"Play",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
                Log.i(TAG, "Activated:" + playButton.isEnabled());
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
                startActivity(new Intent(getActivity().getApplicationContext(), MapsActivity.class));
            }
        });

        final ImageButton droneBtn = rootView.findViewById(R.id.drone_btn);
        droneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), DroneActivity.class));
            }
        });

       /* Button pauseButton=(Button) rootView.findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this,"Pause",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
            }
        });*/

        return rootView;
    }

   /* private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }*/

   /* private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
            toggleButtonImage();
        }
    }*/

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

}
