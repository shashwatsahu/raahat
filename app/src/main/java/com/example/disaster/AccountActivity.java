package com.example.disaster;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AccountActivity extends Fragment {

    public static int GET_FROM_GALLERY = 3;

    public AccountActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        ImageButton productImgBtn = rootView.findViewById(R.id.profile_img_btn);

        registerForContextMenu(productImgBtn);

        productImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.showContextMenu();

            }
        });

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.take_pic:
                //editNote(info.id);
                return true;
            case R.id.choose_from_album:
                //  deleteNote(info.id);

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


}
