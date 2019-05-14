package com.swimlab.gallery;

import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;



public class PropertiesViewFragment extends Fragment {

    private Uri pictureUri;
    private TabViewAdapter adapter;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        Log.d("properties","onCreate");


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        assert getArguments() != null;
        pictureUri = Uri.parse(getArguments().getString("uri"));

        View detailsView = inflater.inflate(R.layout.details_view_fragment,container,false);

        ViewPager viewPager = detailsView.findViewById(R.id.properties_view_pager);
        adapter = new TabViewAdapter(getContext(),getFragmentManager());

        Log.d("properties","adapter created");
        adapter.setPictureUri(pictureUri);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = detailsView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        return detailsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        Log.d("PropertiesFragment","notify");

    }
}
