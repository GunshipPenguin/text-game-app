package com.gunshippenguin.textgame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.TimeUnit;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class TextGameMainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int PERMISSIONS_REQUEST_LOCATION = 2;
    private static final String TIME_FORMAT = "%02d:%02d:%02d";
    private static final String[] CAPTURE_POINT_LABELS = {"A", "B", "C", "D", "E"};

    GoogleMap map;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker[] capturePointMarkers = new Marker[3];
    Marker[] playerMarkers = new Marker[2];

    TextView countdown;
    EditText messageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_game_main);

        // Map Fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // List View
        String[] myList = new String[] {getContactNameByNumber("6477799320"),"World","Foo","Bar"};
        ListView lv = (ListView) this.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myList));

        // Message actionSend
        final Button sendMessageButton = (Button)findViewById(R.id.sendMessage);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the message
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendMessageButton.getWindowToken(), 0); // close keyboard
                messageField.setText(""); // clear
            }
        });

        messageField = (EditText) this.findViewById(R.id.messageField);

        // Make sure input doesn't appear on start
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        // Start timer
        countdown = (TextView)findViewById(R.id.countdownTimer);

        new CountDownTimer(16069000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                countdown.setText("" + String.format(TIME_FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                countdown.setText("Game Over!");
            }
        }.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        try {
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

        } catch (Resources.NotFoundException e) {}

        googleMap.getUiSettings().setAllGesturesEnabled(true);

        map.animateCamera(CameraUpdateFactory.zoomTo(20), 500, null);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }

        // Set up capture points
        for(int i = 0; i < 3; i++){
            capturePointMarkers[i] = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(  43.6677,-79.3948)) // temp locations
                    .title(CAPTURE_POINT_LABELS[i])
                    .icon(getMarkerIcon("#E53935"))
                    );
            capturePointMarkers[i].showInfoWindow();
        };

        // Set up user locations
        for(int i = 0; i < 2; i++){
            playerMarkers[i] = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(43.6277, -79.3948)) // temp locations
                            .icon(getMarkerIcon("#1976D2"))
                    .title("{name}")
                    );
        };
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location){
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        // Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(getMarkerIcon("#4FC3F7"));
        mCurrLocationMarker = map.addMarker(markerOptions);

        // move map camera
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

    }

    public String getContactNameByNumber(String number) {
        String name = "?";

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

            ContentResolver contentResolver = getContentResolver();
            Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                    ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

            try {
                if (contactLookup != null && contactLookup.getCount() > 0) {
                    contactLookup.moveToNext();
                    name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    // String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                }
            } finally {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }

        }
        return name;
    }


    public void updateTeamScore(String toThis) {
        TextView textView = (TextView) findViewById(R.id.enemyScore);
        textView.setText(toThis);
    }

    public void updateEnemyScore(String toThis) {
        TextView textView = (TextView) findViewById(R.id.teamScore);
        textView.setText(toThis);
    }

    public Location getLocation(){
        return map.getMyLocation();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TextGameMainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Until you grant the permission, we can't get your friends' names", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_LOCATION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay!
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient();
                    }
                    map.setMyLocationEnabled(true);
                }

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    // helper for coloring markers
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

}