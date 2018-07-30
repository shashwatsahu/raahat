package com.example.disaster;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private FloatingActionButton floatingActionButton;

    private TabLayout.Tab homeTab, notificationTab, accountTab;

    private int[] tabIcons = {
            R.drawable.ic_home, R.drawable.ic_notification_dark, R.drawable.ic_account_dark};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        // ViewPagerAdapter adapter = new ViewPagerAdapter( this,getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tab_layout);

        floatingActionButton = findViewById(R.id.floating_btn);

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

        tab();
    }

    public void tab(){
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        homeTab.setIcon(R.drawable.ic_home);
                        floatingActionButton.setVisibility(View.VISIBLE);
                        break;

                    case 1:
                        notificationTab.setIcon(R.drawable.ic_notification);
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        break;

                    case 2:
                        accountTab.setIcon(R.drawable.ic_account);
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        homeTab.setIcon(R.drawable.ic_home_dark);
                        break;

                    case 1:
                        notificationTab.setIcon(R.drawable.ic_notification_dark);

                    case 2:
                        accountTab.setIcon(R.drawable.ic_account_dark);
                        break;

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void setupTabIcons() {
        tabLayout = findViewById(R.id.tab_layout);
        homeTab = tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        notificationTab = tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        accountTab = tabLayout.getTabAt(2).setIcon(tabIcons[2]);

    }
    public void setupViewPager(ViewPager viewPager){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeActivity());
        adapter.addFrag(new NotificationActivity());
        adapter.addFrag(new AccountActivity());
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mfragmentlist =new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mfragmentlist.get(position);
        }

        @Override
        public int getCount() {
            return mfragmentlist.size();
        }
        public void addFrag(Fragment fragment){
            mfragmentlist.add(fragment);
        }

    }


}
