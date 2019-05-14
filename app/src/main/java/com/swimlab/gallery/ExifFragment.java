package com.swimlab.gallery;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
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


public class ExifFragment extends Fragment {


    ExifInterface exifInterface;
    InputStream is;
    TextView textView;
    Uri pictureUri;

    static HashMap<String,String> stringTags;
    static HashMap<String,String> doubleTags;
    static HashMap<String,String> intTags;

    static {
        stringTags = new LinkedHashMap<>();
        stringTags.put(ExifInterface.TAG_ARTIST,"Artysta: ");
        stringTags.put(ExifInterface.TAG_DATETIME,"Date: ");
        stringTags.put(ExifInterface.TAG_CFA_PATTERN,"CFA pattern: ");
        stringTags.put(ExifInterface.TAG_COPYRIGHT,"Copyright: ");
        stringTags.put(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION,"Device setting description: ");
        stringTags.put(ExifInterface.TAG_EXIF_VERSION,"Exif version: ");
        stringTags.put(ExifInterface.TAG_GPS_ALTITUDE,"Altitude: ");
        stringTags.put(ExifInterface.TAG_GPS_LATITUDE,"Latitude: ");
        stringTags.put(ExifInterface.TAG_GPS_LONGITUDE,"Longitude: ");
        //stringTags.put(ExifInterface.TAG_GPS_DATESTAMP,"Date: ");

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uriString = (String) getArguments().get("uri");
        pictureUri = Uri.parse(uriString);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View exifView = inflater.inflate(R.layout.fragment_exif, container, false);
        textView = exifView.findViewById(R.id.exif_textview);

        return exifView;
    }

    @Override
    public void onResume() {
        super.onResume();
        createExifInterface();
        appendDoubleAttributes();
        appendIntAttributes();
        appendStringAttributes();

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
            String attribute = (String)tagPair.getKey();
            attributeVal = exifInterface.getAttribute(attribute);

            if(attributeVal!=null) {


                builder.append(tagPair.getValue());
                builder.append(" ");
                if(attribute.equals(ExifInterface.TAG_GPS_LONGITUDE)
                        ||attribute.equals(ExifInterface.TAG_GPS_LATITUDE)
                        ||attribute.equals(ExifInterface.TAG_GPS_ALTITUDE))
                {
                    builder.append(parseGPS(attributeVal));
                    if(attribute.equals(ExifInterface.TAG_GPS_ALTITUDE))
                        builder.append(" m");
                    builder.append("\n");
                }
                else {

                    builder.append(attributeVal);
                    builder.append("\n");
                }
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
    private String parseGPS(String orgiginalCoordinates)
    {
        StringBuilder result = new StringBuilder();
        String [] pairs = orgiginalCoordinates.split(",");

        for(String s : pairs)
        {
            String [] pair = s.split("/");
            double value = Double.valueOf(pair[0])/Double.valueOf(pair[1]);
            result.append(value);
            result.append(" ");
        }
        result.setCharAt(result.length()-1,' ');
        return result.toString();
    }

}
