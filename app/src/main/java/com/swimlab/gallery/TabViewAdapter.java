package com.swimlab.gallery;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TabViewAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Uri pictureUri;

    public TabViewAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        this.context=context;
    }

    @Override
    public Fragment getItem(int i) {

        Bundle bundle = new Bundle();
        bundle.putString("uri",pictureUri.toString());
        switch (i)
        {
            case 0:
                DetailsFragment detailsFragment =  new DetailsFragment();
                detailsFragment.setArguments(bundle);
                return detailsFragment;

            case 1:
                ExifFragment exifFragment = new ExifFragment();
                exifFragment.setArguments(bundle);
                return exifFragment;
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return context.getString(R.string.details_string);
            default:
                return  context.getString(R.string.exif_string);
        }
    }
    @Override
    public int getItemPosition(Object object){
        return FragmentPagerAdapter.POSITION_NONE;
    }
    public void setPictureUri(Uri pictureUri)
    {
        this.pictureUri=pictureUri;
    }
}
