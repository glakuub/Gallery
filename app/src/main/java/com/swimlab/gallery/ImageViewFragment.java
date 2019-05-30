package com.swimlab.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ImageViewFragment extends Fragment {
    List<Photo> picturesList;
    int currentPicture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currentPicture = getArguments().getInt("index");
        setRetainInstance(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.preview_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        Uri pictureUri=picturesList.get(currentPicture).getUri();
        switch (menuItem.getItemId())
        {
            case R.id.setWallpaper:
                setAsWallpaper(pictureUri);
                return true;
            case R.id.sendPhoto:
                sendPhoto(pictureUri);
                return true;
            case R.id.seeDetails:
                seePhotoDetails(pictureUri);
                return true;
            case R.id.cropImage:
                cropImage(pictureUri);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }
    private void cropImage(Uri pictureUri)
    {
        CropImage.activity(pictureUri).start(getContext(),this);
    }
    private void seePhotoDetails(Uri pictureUri)
    {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        PropertiesViewFragment fragment = new PropertiesViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("uri",pictureUri.toString());
        fragment.setArguments(bundle);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framgment_container,fragment,"details")
                .addToBackStack("preview")
                .commit();

    }

    private void setAsWallpaper(Uri uri)
    {
        Intent wallpaperIntent = new Intent();
        wallpaperIntent.setAction(Intent.ACTION_ATTACH_DATA);
        wallpaperIntent.setDataAndType(uri,"image/*");
        startActivity(Intent.createChooser(wallpaperIntent,getResources().getText(R.string.set_as)));
    }
    private void sendPhoto(Uri uri)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent,getResources().getText(R.string.send_to)));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {




        View fragmentView = inflater.inflate(R.layout.image_view_fragment, container, false);
        ViewPager viewPager = fragmentView.findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getContext(),picturesList,this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPicture);

        androidx.appcompat.widget.Toolbar toolbar =  fragmentView.findViewById(R.id.image_view_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return fragmentView;
    }
    public void setPicturesList(List<Photo> picturesList)
    {
        this.picturesList=picturesList;
    }
    public void updateCurrentPicture(int index)
    {
        currentPicture=index;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d("uri",resultUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
