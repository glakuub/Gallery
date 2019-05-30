package com.swimlab.gallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.documentfile.provider.DocumentFile;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {

    public enum Layout
    {
        LINEAR,
        GRID;
    }
    private static final String SET_GRID_VIEW ="Set grid view";
    private static final String SET_LIST_VIEW ="Set list view";
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<Photo> picturesList;
    private Uri uri;
    private Layout currentLayoutType=null;
    private static final String NO_DIRECTORY = "no_directory";
    public static final int CHANGE_DIR_CODE =100 ;
    private static final String SAVED_URI_KEY ="saved_uri" ;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int LINEAR=0;
    private static final int GRID=1;
    private static String LAYOUT_TYPE_KEY="l_type";
    private static int SPAN_COUNT=3;
    private Menu menu;
    private String backStackName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        picturesList = new ArrayList<>();
        checkSavedDirectory();
        updatePicturesList(uri);
        currentLayoutType=checkSavedLayout();
    }
    private Layout checkSavedLayout()
    {

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getInt(LAYOUT_TYPE_KEY,LINEAR)==0?Layout.LINEAR:Layout.GRID;
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {


        View view = layoutInflater.inflate(R.layout.recycler_view_fragment,viewGroup,false);

        adapter= new ImageAdapter(picturesList,getContext(),currentLayoutType);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        //currentLayoutType = checkSavedLayout();
        if(currentLayoutType==Layout.LINEAR)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),SPAN_COUNT));
        }

        setOnElementClickListener((int i)->sendToDisplay(i));

        androidx.appcompat.widget.Toolbar toolbar =  view.findViewById(R.id.galleryToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        updateGalleryContent();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        this.menu=menu;
        inflater.inflate(R.menu.gallery_menu, menu);
        MenuItem switchButton = menu.findItem(R.id.switchLayout);

        if(currentLayoutType==Layout.LINEAR)
        {
            switchButton.setTitle(SET_GRID_VIEW);
        }
        else
        {
            switchButton.setTitle(SET_LIST_VIEW);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {

        switch (menuItem.getItemId())
        {
            case R.id.switchLayout:
                switchLayoutManager();
                return true;

            case R.id.changeDirectory:
                chooseDirectory();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    public void switchLayoutManager() {


        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        MenuItem switchButton = menu.findItem(R.id.switchLayout);

        if(currentLayoutType==Layout.GRID)
        {

            currentLayoutType=Layout.LINEAR;
            recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));

            adapter = new ImageAdapter( picturesList,getContext(),currentLayoutType);
            setOnElementClickListener((int i)->sendToDisplay(i));
            recyclerView.setAdapter(adapter);
            //adapter.switchLayoutType();


            switchButton.setTitle(SET_GRID_VIEW);
            editor.putInt(LAYOUT_TYPE_KEY,LINEAR);
            editor.commit();



        }
        else if(currentLayoutType==Layout.LINEAR)
        {

            currentLayoutType=Layout.GRID;
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),SPAN_COUNT));

            adapter = new ImageAdapter( picturesList,getContext(),currentLayoutType);
            //adapter.switchLayoutType();
            setOnElementClickListener((int i)->sendToDisplay(i));
            recyclerView.setAdapter(adapter);


            switchButton.setTitle(SET_LIST_VIEW);
            editor.putInt(LAYOUT_TYPE_KEY,GRID);
            editor.commit();

        }
    }

    private void setOnElementClickListener(ImageAdapter.OnClickListener listener)
    {
        adapter.setOnClickListener(listener);

    }
    private void sendToDisplay(int i)
    {

        ImageViewFragment imageViewFragment = new ImageViewFragment();
        imageViewFragment.setPicturesList(picturesList);
        Bundle bundle = new Bundle();
        bundle.putInt("index",i);
        imageViewFragment.setArguments(bundle);
        AppCompatActivity activity = (AppCompatActivity) getActivity();


        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framgment_container,imageViewFragment,"image view")
                .addToBackStack("image view")
                .commit();


    }
    private void checkSavedDirectory()
    {

        String savedUri=checkSavedUriString();

        if(savedUri== NO_DIRECTORY)
        {
            Intent directoryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(directoryIntent,CHANGE_DIR_CODE);

        }
        else
        {
            uri=Uri.parse(savedUri);

        }

    }

    private String checkSavedUriString()
    {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(SAVED_URI_KEY, NO_DIRECTORY);
    }

    private ArrayList<Photo> getPhotos(Uri uri)
    {
        ArrayList<Photo> photos = new ArrayList<>();
        if(uri!=null) {

            DocumentFile documentFile = DocumentFile.fromTreeUri(getContext(), uri);

            for (DocumentFile file : documentFile.listFiles()) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {
                        photos.add(new Photo(file.getName(), file.getUri()));
                    }
                }
            }
        }
        return photos;
    }

    public void chooseDirectory() {

        Intent directoryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(directoryIntent,CHANGE_DIR_CODE);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RecyclerViewFragment.CHANGE_DIR_CODE)
        {

            if(resultCode==getActivity().RESULT_OK)
            {
                Uri uri = data.getData();
                Log.d("uri",uri.toString());
                updatePicturesList(uri);
                updateGalleryContent();

            }
        }
    }

    public void updateGalleryContent()
    {


        adapter.notifyDataSetChanged();


    }
    public void updatePicturesList(Uri uri)
    {
        this.uri = uri;
        picturesList.clear();
        picturesList.addAll(getPhotos(uri));
        saveUri(uri);
    }
    private void saveUri(Uri uri)
    {
        if(uri!=null) {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SAVED_URI_KEY, uri.toString());
            editor.commit();
        }
    }

}























  /*
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        }
        else
        {

            setupRecyclerView();
            checkSavedDirectory();

        }




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu=menu;

        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        MenuItem switchButton = menu.findItem(R.id.switchLayout);

        if(currentLayoutType== MainActivity.Layout.LINEAR)
        {
            switchButton.setTitle(SET_GRID_VIEW);
        }
        else
        {
            switchButton.setTitle(SET_LIST_VIEW);
        }

        return true;
    }
    private void checkSavedDirectory()
    {
        String savedUri=checkSavedUriString();

        if(savedUri== NO_DIRECTORY)
        {
            Intent directoryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(directoryIntent,CHANGE_DIR_CODE);

        }
        else
        {
            uri=Uri.parse(savedUri);
            updateGalleryContent();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_EXTERNAL_STORAGE:
            {
                setupRecyclerView();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    String savedUri=checkSavedUriString();

                    if(savedUri== NO_DIRECTORY)
                    {
                        Intent directoryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(directoryIntent,CHANGE_DIR_CODE);

                    }
                    else
                    {
                        uri=Uri.parse(savedUri);
                        updateGalleryContent();

                    }


                }

                return;
            }

        }
    }

    public void switchLayoutManager() {


        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        MenuItem switchButton = menu.findItem(R.id.switchLayout);

        if(currentLayoutType== MainActivity.Layout.GRID)
        {

            currentLayoutType= MainActivity.Layout.LINEAR;
            recyclerView.setLayoutManager( new LinearLayoutManager(this));

            adapter = new ImageAdapter( picturesList,this,currentLayoutType);
            adapter.setOnClickListener((Photo photo)->{
                sendToDisplay(photo);});

            recyclerView.setAdapter(adapter);


            switchButton.setTitle(SET_GRID_VIEW);
            editor.putInt(LAYOUT_TYPE_KEY,LINEAR);
            editor.commit();



        }
        else if(currentLayoutType== MainActivity.Layout.LINEAR)
        {

            currentLayoutType= MainActivity.Layout.GRID;
            recyclerView.setLayoutManager(new GridLayoutManager(this,SPAN_COUNT));

            adapter = new ImageAdapter( picturesList,this,currentLayoutType);
            adapter.setOnClickListener((Photo photo)->{
                sendToDisplay(photo);});

            recyclerView.setAdapter(adapter);

            switchButton.setTitle(SET_LIST_VIEW);
            editor.putInt(LAYOUT_TYPE_KEY,GRID);
            editor.commit();

        }
    }

    private int checkSavedLayout()
    {

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getInt(LAYOUT_TYPE_KEY,LINEAR);
    }

    private String checkSavedUriString()
    {
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(SAVED_URI_KEY, NO_DIRECTORY);
    }
    private void saveUri(Uri uri)
    {
        if(uri!=null) {
            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SAVED_URI_KEY, uri.toString());
            editor.commit();
        }
    }

    private void setupRecyclerView()
    {
        MenuItem switchbutton=null;
        recyclerView = findViewById(R.id.mainAcitivityRecyclerView);

        if(checkSavedLayout()==LINEAR)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            currentLayoutType= MainActivity.Layout.LINEAR;

        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this,SPAN_COUNT));
            currentLayoutType= MainActivity.Layout.GRID;

        }

        adapter = new ImageAdapter( picturesList,this,currentLayoutType);
        adapter.setOnClickListener((Photo photo)->{
            sendToDisplay(photo);});

        recyclerView.setAdapter(adapter);
    }

    private void sendToDisplay(Photo photo)
    {
        Intent intent = new Intent(this, PictureDisplayActivity.class);
        intent.putExtra(PictureDisplayActivity.PASS_PICTURE,photo.getUri().toString());
        startActivity(intent);

    }






    private ArrayList<Photo> getPhotos(Uri uri)
    {
        ArrayList<Photo> photos = new ArrayList<>();
        if(uri!=null) {

            DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);

            for (DocumentFile file : documentFile.listFiles()) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {
                        photos.add(new Photo(file.getName(), file.getUri()));
                    }
                }
            }
        }
        return photos;
    }




    public void chooseDirectory() {

        Intent directoryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(directoryIntent,CHANGE_DIR_CODE);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intetn)
    {
        if(requestCode==CHANGE_DIR_CODE)
        {

            if(resultCode==RESULT_OK)
            {
                uri = intetn.getData();
                updateGalleryContent();

            }
        }
    }
    private void updateGalleryContent()
    {
        picturesList.clear();
        picturesList.addAll(getPhotos(uri));
        adapter.notifyDataSetChanged();
        saveUri(uri);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {

        switch (menuItem.getItemId())
        {
            case R.id.switchLayout:
                switchLayoutManager();
                return true;

            case R.id.changeDirectory:
                chooseDirectory();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }
*/