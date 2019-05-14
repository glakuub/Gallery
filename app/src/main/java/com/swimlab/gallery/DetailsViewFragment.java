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



public class DetailsViewFragment extends Fragment {

    private Uri pictureUri;
    ExifInterface exifInterface;
    InputStream is;
    TextView textView;

    static HashMap<String,String> stringTags;
    static HashMap<String,String> doubleTags;
    static HashMap<String,String> intTags;

    static {
        stringTags = new LinkedHashMap<>();
        stringTags.put(ExifInterface.TAG_ARTIST,"Artysta:");
        stringTags.put(ExifInterface.TAG_DATETIME,"Date:");
        stringTags.put(ExifInterface.TAG_CFA_PATTERN,"1:");
        stringTags.put(ExifInterface.TAG_COPYRIGHT,"3:");
        stringTags.put(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION,"4:");
        stringTags.put(ExifInterface.TAG_EXIF_VERSION,"Exif version:");

        intTags = new LinkedHashMap<>();
        intTags.put(ExifInterface.TAG_BITS_PER_SAMPLE,"Bits: ");
        intTags.put(ExifInterface.TAG_COMPRESSION,"Compression: ");
        intTags.put(ExifInterface.TAG_CONTRAST,"Contrast: ");
        intTags.put(ExifInterface.TAG_IMAGE_LENGTH,"Image length: ");
        intTags.put(ExifInterface.TAG_IMAGE_WIDTH,"Image width: ");
        intTags.put(ExifInterface.TAG_PLANAR_CONFIGURATION,"Planar configuration: ");
        intTags.put(ExifInterface.TAG_SATURATION,"Saturation: ");
        intTags.put(ExifInterface.TAG_WHITE_BALANCE,"White balance: ");

        doubleTags = new LinkedHashMap<>();
        doubleTags.put(ExifInterface.TAG_BRIGHTNESS_VALUE,"Brightness: ");
        doubleTags.put(ExifInterface.TAG_APERTURE_VALUE,"Aperture: ");
        doubleTags.put(ExifInterface.TAG_EXPOSURE_INDEX,"Exposure index: ");
        doubleTags.put(ExifInterface.TAG_GPS_SPEED,"GPS speed: ");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        pictureUri = Uri.parse(getArguments().getString("uri"));
        createExifInterface();
        setRetainInstance(true);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View detailsView = inflater.inflate(R.layout.details_view_fragment,container,false);

        textView = detailsView.findViewById(R.id.details_text);
        appendDetails();
        appendStringAttributes();
        appendIntAttributes();
        appendDoubleAttributes();


        return detailsView;
    }
    private void createExifInterface()
    {

        try
        {
            is = getActivity().getContentResolver().openInputStream(pictureUri);
            exifInterface = new ExifInterface(is);

        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            if(is!=null) {
                try {

                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private void appendStringAttributes()
    {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = stringTags.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry tagPair= (Map.Entry)iterator.next();
            String attributeVal;
            attributeVal = exifInterface.getAttribute((String)tagPair.getKey());

            if(attributeVal!=null) {
                builder.append(tagPair.getValue());
                builder.append(" ");
                builder.append(attributeVal);
                builder.append("\n");
            }
        }
        textView.append(builder.toString());
    }
    private void appendIntAttributes()
    {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = intTags.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry tagPair= (Map.Entry)iterator.next();
            int attributeVal;
            attributeVal = exifInterface.getAttributeInt((String) tagPair.getKey(),0);

            if(attributeVal!=0) {
                builder.append(tagPair.getValue());
                builder.append(" ");
                builder.append(attributeVal);
                builder.append("\n");

            }

        }
        textView.append(builder.toString());
    }
    private void appendDoubleAttributes()
    {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = doubleTags.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry tagPair= (Map.Entry)iterator.next();
            double attributeVal;
            attributeVal = exifInterface.getAttributeDouble((String) tagPair.getKey(),0);

            if(attributeVal!=0) {

                builder.append(tagPair.getValue());
                builder.append(" ");
                builder.append(attributeVal);
                builder.append("\n");
            }

        }
        textView.append(builder.toString());
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
        cursor.close();
        builder.append("Name: ");
        builder.append(cursor.getString(nameIndex));
        builder.append("\n");
        builder.append("Size: ");
        builder.append(cursor.getString(sizeIndex));
        builder.append("\n");
        builder.append("Type: ");
        builder.append(getActivity().getContentResolver().getType(pictureUri));
        builder.append("\n");

        textView.append(builder.toString());

    }
}
