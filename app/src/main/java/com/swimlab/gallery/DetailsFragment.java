package com.swimlab.gallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailsFragment extends Fragment {


    private Uri pictureUri;
    private TextView textView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uriString = (String) getArguments().get("uri");
        pictureUri = Uri.parse(uriString);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.fragment_details, container, false);
        textView = detailsView.findViewById(R.id.details_texview);
        appendDetails();

        return detailsView;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void appendDetails()
    {
        StringBuilder builder = new StringBuilder();
        Cursor cursor = getActivity().getContentResolver().query(pictureUri,null,null,null,null);
        assert cursor != null;
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        String sizeString = cursor.getString(sizeIndex);
        float sizeInKB = Float.valueOf(sizeString)/1048576;

        builder.append("Name: ");
        builder.append(name);
        builder.append("\n");
        builder.append("Size: ");
        builder.append(sizeInKB);
        builder.append(" MB\n");
        builder.append("Type: ");
        builder.append(getActivity().getContentResolver().getType(pictureUri));
        builder.append("\n");
        cursor.close();

        textView.append(builder.toString());

    }



}
