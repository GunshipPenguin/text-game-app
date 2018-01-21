package com.gunshippenguin.textgame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.gunshippenguin.textgame.events.CaptureTreasureEvent;
import com.gunshippenguin.textgame.events.ChatMessageEvent;
import com.gunshippenguin.textgame.events.DisplayableInterface;
import com.gunshippenguin.textgame.events.Event;
import com.gunshippenguin.textgame.events.GameInfoEvent;
import com.gunshippenguin.textgame.events.GameStartingEvent;
import com.gunshippenguin.textgame.events.InvalidEventException;
import com.gunshippenguin.textgame.events.PositionUpdateEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class TextGameMainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int TREASURE_DISTANCE_THRESHOLD_M = 12;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int PERMISSIONS_REQUEST_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 2;

    private static final String TIME_FORMAT = "%02d:%02d:%02d";
    private static final String[] CAPTURE_POINT_LABELS = {"A", "B", "C", "D", "E"};


    private List<String> mPlayerNumbers;
    private List<TreasureSpawn> mTreasureSpawns;

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

    public ArrayList<DisplayableInterface> eventData;
    private RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    Timer backgroundEventsTimer;
    TimerTask broadcastPositionTask;
    TimerTask checkCaptureTreasureTask;

    private GameTextReceiver mReceiver = null;


    int ourScore = 0;
    Map<String, Integer> playerScores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_game_main);

        Bundle gameData = getIntent().getBundleExtra(GameStartingEvent.BUNDLE_KEY);

        mPlayerNumbers = (ArrayList<String>) gameData.getSerializable(GameStartingEvent.PLAYER_NUMBERS_KEY);
        for (String number : mPlayerNumbers) {
            playerScores.put(number, 0);
        }

        mTreasureSpawns = (ArrayList<TreasureSpawn>) gameData.getSerializable(GameStartingEvent.TREASURE_SPAWNS_KEY);

        // Map Fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // List View
        mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);

        // specify an adapter (see also next example)
        eventData = new ArrayList<DisplayableInterface>();

        mAdapter = new StreamAdapter(getApplicationContext(), eventData);
        mRecyclerView.setAdapter(mAdapter);

        // Load a start message
        GameInfoEvent startingEvent = new GameInfoEvent("1", "Game started! Go get as many points as you can!");
        eventData.add((DisplayableInterface)startingEvent);
        mAdapter.notifyItemInserted(0);

        // Message actionSend
        final Button sendMessageButton = (Button)findViewById(R.id.sendMessage);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageField.getText().toString();
                if (message.length() > 0){

                    //create message event
                    ChatMessageEvent sentSMS = new ChatMessageEvent("0", message);
                    sentSMS.sendToNumbers(mPlayerNumbers, getApplicationContext());
                    eventData.add((DisplayableInterface)sentSMS);
                    mAdapter.notifyItemInserted(0);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sendMessageButton.getWindowToken(), 0); // close keyboard
                    messageField.setText(""); // clear
                }
            }
        });

        messageField = (EditText) this.findViewById(R.id.messageField);

        // Make sure input doesn't appear on start
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        // Start timer
        countdown = (TextView)findViewById(R.id.countdownTimer);

        new CountDownTimer(900000, 1000) { // adjust the milli seconds here

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


        if (mReceiver == null) {
            mReceiver = new GameTextReceiver();
            registerReceiver(mReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }

        // Background tasks
        backgroundEventsTimer = new Timer();
        broadcastPositionTask = new TimerTask() {
            @Override
            public void run() {
                broadcastPosition();
            }
        };
        checkCaptureTreasureTask = new TimerTask() {
            @Override
            public void run() {
                checkCaptureTreasure();
            }
        };

        backgroundEventsTimer.scheduleAtFixedRate(broadcastPositionTask, 60000, 60000);
        backgroundEventsTimer.scheduleAtFixedRate(checkCaptureTreasureTask, 2000, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReceiver == null) {
            mReceiver = new GameTextReceiver();
            registerReceiver(mReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
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
                            .icon(getMarkerIcon("#1565C0"))
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
        markerOptions.title("Your location");
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

    private void broadcastPosition() {
        Event positionUpdateEvent = new PositionUpdateEvent("111", new Date(),
                mLastLocation.getLatitude(), mLastLocation.getLongitude());
        positionUpdateEvent.sendToNumbers(getPlayerNumbers(), this);
    }

    private void broadcastTreasureCaptured(int number) {
        Event captureTreasureEvent = new CaptureTreasureEvent("111", new Date(), number);
        captureTreasureEvent.sendToNumbers(getPlayerNumbers(), this);
    }

    private void checkCaptureTreasure() {
        Date currTime = new Date();

        for (int i=0;i<mTreasureSpawns.size();i++) {
            TreasureSpawn ts = mTreasureSpawns.get(i);

            if (!ts.isTaken() && (ts.getTime().before(currTime))) {
                float distances[] = new float[3];
                Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), ts.getLat(), ts.getLong(), distances);
                if (distances[0] < TREASURE_DISTANCE_THRESHOLD_M) {
                    ourScore += 1;
                    TextView t = (TextView) this.findViewById(R.id.teamScore);
                    String scoreString = Integer.toString(ourScore);
                    t.setText(scoreString);
                    ts.setTaken();
                    broadcastTreasureCaptured(i);
                    break;
                }
            }
        }
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
        } else if (requestCode == PERMISSIONS_REQUEST_SEND_SMS){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Until you grant the permission, the game can't be played", Toast.LENGTH_SHORT).show();
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

    public class GameTextReceiver extends BroadcastReceiver {
        private PendingResult mPendingResult;
        @Override
        public void onReceive(final Context context, final Intent intent) {
            mPendingResult = goAsync();
            AsyncTask<Void,Void,List<Event> > asyncHandler = new AsyncTask<Void, Void, List<Event> >() {
                @Override
                protected List<Event> doInBackground(Void... voids) {
                    List<Event> events = new ArrayList<Event>();
                    if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                        StringBuilder m = new StringBuilder();
                        for (int i = 0; i < messages.length; i++) {
                            m.append(messages[i].getMessageBody());
                        }
                        //for (int i = 0; i < messages.length; ++i) {
                        // if the message is from someone unexpected, this will error out
                        // because we won't be able to parse the format
                        Boolean failed = false;
                        Inflater decompresser = new Inflater();
                        byte[] messageText = Base64.decode(m.toString(),Base64.DEFAULT);
                        decompresser.setInput(messageText,0,messageText.length);
                        byte[] result = new byte[1024];
                        StringBuilder stringifiedJSON = new StringBuilder();
                        while (!decompresser.finished()) {
                            try{
                                decompresser.inflate(result);
                            } catch(DataFormatException e) {
                                failed = true;
                                break;
                            }

                            stringifiedJSON.append(new String(result));
                        }

                        if (!failed) {
                            try {
                                JSONObject event = new JSONObject(stringifiedJSON.toString());
                                String addr = messages[0].getDisplayOriginatingAddress();
                                events.add(Event.fromJson(addr,event));
                            } catch(JSONException e) {
                                Log.e("BROKEN_JSON",e.getMessage());
                            } catch(InvalidEventException e2) {
                                Log.e("BROKEN_EVENT",e2.getMessage());
                            }
                        }
                        //}
                    }

                    return events;
                }

                @Override
                protected void onPostExecute(List<Event> events) {
                    if (events != null) {
                        for (int i = 0; i < events.size(); ++i) {
                            events.get(i).handleEvent(TextGameMainActivity.this);
                        }
                    }
                    mPendingResult.finish();
                }
            };
            asyncHandler.execute();

        }
    }


    // private void sendChatMessage(String phoneNumber, String message) {
    //     SmsManager sms = SmsManager.getDefault();
    //
    //     try {
    //         JSONObject json = new JSONObject();
    //         json.put("event_type", "chat_message");
    //         json.put("message", message);
    //         String b64Message = Base64.encode(json.toString().getBytes(), Base64.DEFAULT).toString();
    //
    //         sms.sendTextMessage(phoneNumber, null, b64Message, null, null);
    //
    //     } catch (Exception e){};
    //
    // }

    public List<String> getPlayerNumbers(){
        return mPlayerNumbers;
    }

    public void removeTreasureAtIndex(int i) {
        mTreasureSpawns.get(i).setTaken();
    }

    public void incrementPlayerByNumber(String phoneNumber) {
        int curScore = playerScores.get(phoneNumber);
        playerScores.put(phoneNumber,curScore + 1);
    }

    public void setEnemyScore() {
        int max = 0;
        for (String key : playerScores.keySet()) {
            if (playerScores.get(key) > max) {
                max = playerScores.get(key);
            }
        }
        TextView t = (TextView) this.findViewById(R.id.enemyScore);
        t.setText(Integer.toString(max));
    }

}