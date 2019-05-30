package com.swimlab.gallery;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;




public class MainActivity extends AppCompatActivity {

    RecyclerViewFragment recyclerViewFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.framgment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }
        }

        recyclerViewFragment = new RecyclerViewFragment();

        getSupportFragmentManager().beginTransaction().
                add(R.id.framgment_container, recyclerViewFragment,"list fragment")
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);


    }

}
















 /*
    public enum Layout
    {
        LINEAR,
        GRID;
    }
    private static final String NO_DIRECTORY = "no_directory";
    private static final int CHANGE_DIR_CODE =100 ;
    private static final String SET_GRID_VIEW ="Set grid view";
    private static final String SET_LIST_VIEW ="Set list view";
    private static final String SAVED_URI_KEY ="saved_uri" ;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<Photo> picturesList;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int LINEAR=0;
    private static final int GRID=1;
    private static String LAYOUT_TYPE_KEY="l_type";
    private static int SPAN_COUNT=3;
    private Menu menu;
    private Uri uri;
    private Layout currentLayoutType=null;
    */
 /*
        setContentView(R.layout.activity_main);
        picturesList= new ArrayList<>();
        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.galleryToolbar);
        setSupportActionBar(toolbar);


            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED)
                {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_EXTERNAL_STORAGE);
                }
            else
                {

                    setupRecyclerView();
                    checkSavedDirectory();

                }


            */


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu=menu;

        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        MenuItem switchButton = menu.findItem(R.id.switchLayout);

        if(currentLayoutType==Layout.LINEAR)
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

        if(currentLayoutType==Layout.GRID)
        {

            currentLayoutType=Layout.LINEAR;
            recyclerView.setLayoutManager( new LinearLayoutManager(this));

            adapter = new ImageAdapter( picturesList,this,currentLayoutType);
            adapter.setOnClickListener((Photo photo)->{
                sendToDisplay(photo);});

            recyclerView.setAdapter(adapter);


            switchButton.setTitle(SET_GRID_VIEW);
            editor.putInt(LAYOUT_TYPE_KEY,LINEAR);
            editor.commit();



        }
        else if(currentLayoutType==Layout.LINEAR)
        {

            currentLayoutType=Layout.GRID;
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
            currentLayoutType=Layout.LINEAR;

        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this,SPAN_COUNT));
            currentLayoutType=Layout.GRID;

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