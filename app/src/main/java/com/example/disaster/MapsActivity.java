package com.example.disaster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.util.ArrayList;

/**
 * This sample demonstrates how to use information from a GEOJson feed. For this
 * example, seven days significant earthquakes feed from USGS is used which is
 * updated every minute. Json parser is used to parse the GEOJson feed.
 *
 */

public class MapsActivity extends AppCompatActivity implements ItemAdapter.ItemListener{

    private static final String TAG = "MAPSACTIVITY";
    private boolean flag = false;

    private MapView mMapView;

    BottomSheetBehavior behavior;
    RecyclerView recyclerView;
    private ItemAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mMapView = findViewById(R.id.map_view);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_map);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> items = new ArrayList<>();
        items.add("Rescue operator");
        items.add("Medical camps");

        mAdapter = new ItemAdapter(items, this);
        recyclerView.setAdapter(mAdapter);

        ImageButton button =  findViewById(R.id.bottom_list);

        button.setOnClickListener(new View.OnClickListener() {
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

        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
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

        ArcGISMap map = new ArcGISMap(Basemap.createImagery());

        Point point = new Point(-226773, 6550477, SpatialReferences.getWebMercator());
        Viewpoint vp = new Viewpoint(point, 7500);

        // set initial map extent
        map.setInitialViewpoint(vp);

        setupMap();

        addTrailheadsLayer();

        // create a new graphics overlay and add it to the mapview
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsOverlay);

        //create a simple marker symbol
        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED,
                12); //size 12, style of circle

        //add a new graphic with a new point geometry
        Point graphicPoint = new Point(-226773, 6550477, SpatialReferences.getWebMercator());
        Graphic graphic = new Graphic(graphicPoint, symbol);
        graphicsOverlay.getGraphics().add(graphic);

    }

    private void setupMap() {
            if (mMapView != null) {
                Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
                double latitude = 34.05293;
                double longitude = -118.24368;
                int levelOfDetail = 11;
                ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
                mMapView.setMap(map);
            }
        }

    private void addTrailheadsLayer() {
        String url = "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads/FeatureServer/";
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
        ArcGISMap map = mMapView.getMap();
        map.getOperationalLayers().add(featureLayer);
    }

    private void setElevationSource(ArcGISScene scene) {
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource(
                "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
        scene.getBaseSurface().getElevationSources().add(elevationSource);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

    @Override
    public void onItemClick(String item) {

        Snackbar.make(coordinatorLayout,item + " is selected", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

}
