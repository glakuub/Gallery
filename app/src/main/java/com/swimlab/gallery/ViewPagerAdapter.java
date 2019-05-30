package com.swimlab.gallery;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Photo> picturesList;
    private ImageViewFragment parentFragment;

    ViewPagerAdapter(Context context, List<Photo> picturesList, ImageViewFragment parentFragment)
    {
        this.context=context;
        this.picturesList=picturesList;
        this.parentFragment=parentFragment;
    }

    @Override
    public int getCount() {
        int count=0;
        if(picturesList!=null)
                count=picturesList.size();
        return count;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        parentFragment.updateCurrentPicture(position);
        ImageView imageView = new ImageView(context);
        Glide.with(context).load(picturesList.get(position).getUri()).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
