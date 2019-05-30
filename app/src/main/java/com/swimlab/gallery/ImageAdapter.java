package com.swimlab.gallery;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Photo> photosList;
    private OnLongItemClickListener onLongClickListener;
    private OnClickListener onClickListener;
    private Context context;
    private RecyclerViewFragment.Layout layoutType;



    ImageAdapter(List<Photo> picturesList, Context context, RecyclerViewFragment.Layout layoutType)
    {
        this.photosList =picturesList;
        this.context=context;
        this.layoutType=layoutType;


    }

    public void setOnLongClickListener(OnLongItemClickListener longClickListener)
    {
        this.onLongClickListener = longClickListener;
    }

    void setOnClickListener(OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        View view;
        if(layoutType==RecyclerViewFragment.Layout.LINEAR) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image_layout, parent, false);
        }else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image_layout, parent, false);
        }
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i)
    {


        imageViewHolder.textView.setText(photosList.get(i).getName());
        Glide.with(context).load(photosList.get(i).getUri()).centerCrop().into(imageViewHolder.imageView);
        imageViewHolder.bind(i,onClickListener, onLongClickListener);


    }



    @Override
    public int getItemCount()
    {
        int to_return=0;
        if(photosList!=null)
            to_return =photosList.size();
        return to_return;
    }


    static class ImageViewHolder extends RecyclerView.ViewHolder
    {

        TextView textView;
        ImageView imageView;

        ImageViewHolder(View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerImageView);
            textView = itemView.findViewById(R.id.recyclerTextView);
        }

        void bind(int index, OnClickListener listener, OnLongItemClickListener longListener)
        {
            if(listener!=null)
                itemView.setOnClickListener((View view)->listener.onItemClick(index));
            if(longListener!=null)
                itemView.setOnLongClickListener((View view)->{longListener.onLongClick(index);return true;});
        }
    }

    public interface OnClickListener
    {
        void onItemClick(int index);
    }
    public interface OnLongItemClickListener
    {
        void onLongClick(int index);
    }
    public void switchLayoutType()
    {
        if(layoutType==RecyclerViewFragment.Layout.LINEAR) {
           layoutType=RecyclerViewFragment.Layout.GRID;
        }else
        {
            layoutType=RecyclerViewFragment.Layout.LINEAR;
        }
    }
}
