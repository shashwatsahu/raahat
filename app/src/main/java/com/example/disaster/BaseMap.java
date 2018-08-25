package com.example.disaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class BaseMap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_map);

        WebView myWebView = findViewById(R.id.base_web_view);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://www.arcgis.com/home/webmap/viewer.html?webmap=15caa9a419b54aec8a941bf6ee8dc2ae&extent=-155.4059,19.2338,-155.0015,19.4182");

    }
}
