package com.saud.celebrityapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.Database.OnDataAdded;
import com.saud.celebrityapp.Database.OnDataFetched;
import com.saud.celebrityapp.ui.Wallet.payments.WalletFragment;
import com.saud.celebrityapp.ui.login.LoginActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class MainActivity extends AppCompatActivity implements OnDataFetched, OnDataAdded {
    private static final int REQUEST_CODE_LOCATION = 200;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu=findViewById(R.id.logo);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                finish();
            }
        });
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,
                R.id.navigation_photo, R.id.navigation_videos,R.id.navigation_wallet, R.id.navigation_messages)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Intent intent=getIntent();
        if (intent.getStringExtra("page")!=null){
            String page=intent.getStringExtra("page");
            switch (page){
                case "video":
                    navController.navigate(R.id.navigation_videos);
                    break;
                case "image":
                    navController.navigate(R.id.navigation_photo);
                    break;
                case "message":
                    navController.navigate(R.id.navigation_messages);
                    break;
                case "wallet":
                    navController.navigate(R.id.navigation_wallet);
                    break;

            }

        }

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                    int id=controller.getCurrentDestination().getId();
                TextView textView=findViewById(R.id.title);
                    switch (id){
                        case R.id.navigation_home:
                            textView.setText("Home");
                            break;
                        case R.id.navigation_photo:
                            textView.setText("Photo");
                            break;
                        case R.id.navigation_videos:
                            textView.setText("Videos");
                            break;
                        case R.id.navigation_messages:
                            textView.setText("Chat");
                            break;
                        case R.id.navigation_wallet:
                            textView.setText("Wallet");
                            break;
                    }
            }
        });
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        //getScreenSaverImage();
        check_location_permission();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//         getMenuInflater().inflate(R.menu.main_menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.signout:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this,LoginActivity.class));
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void check_location_permission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            // REQUEST_CODE_LOCATION should be defined on your app level
            ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            check_location_permission();
        }
    }
    @Override
    public void onGetData() {

    }

    @Override
    public void onAddData() {

    }
    public boolean hasPermission(String permission) {
        return  ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
    public void askForMultiplePermissions(){
        final int REQUEST_CODE = 13;
        String internet = Manifest.permission.INTERNET;
        String wallpaper = Manifest.permission.SET_WALLPAPER;


        List<String> permissionList = new ArrayList<>();

        if (!hasPermission(internet)){
            permissionList.add(internet);
        }
        if (!hasPermission(wallpaper)){
            permissionList.add(wallpaper);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[2]);
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE);
        }
       // getScreenSaverImage();
    }
    private void getBitmap(final String strUrl){

        new AsyncTask<Void, String, String>(){
            URL url;
            Bitmap image;
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    url = new URL(strUrl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(IOException e) {
                    System.out.println(e);
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                setScreenSaver(image);
            }
        }.execute(null,null,null);

    }
    private void getScreenSaverImage(){
        db.collection(CollectionNames.col_screensaver).document(CollectionNames.screensaver.doc_name).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              String url=documentSnapshot.get(CollectionNames.screensaver.field_image_url).toString();
                getBitmap(url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setScreenSaver(Bitmap bitmap) {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wall);

        WallpaperManager manager = WallpaperManager.getInstance(MainActivity.this);
        try{
                //manager.setBitmap(bitmap);
                if (manager.isSetWallpaperAllowed()){
                    //manager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                    manager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
                }else{
                    Toast.makeText(this, "Wallpaper is not allowed to be change", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }
}