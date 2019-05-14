package com.swimlab.gallery;

import android.net.Uri;

class Photo {

    private String name;
    private Uri uri;




    Photo(String name, Uri uri)
    {
        this.name=name;
        this.uri=uri;


    }
    String getName()
    {
        return name;
    }
    Uri getUri() {
        return uri;
    }
}
