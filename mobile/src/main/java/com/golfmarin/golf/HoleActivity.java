package com.golfmarin.golf;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.maps.model.LatLng;

import android.content.BroadcastReceiver;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnLongClickListener;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.SortedSet;

import android.util.Log;

public class HoleActivity extends Activity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnLongClickListener
 {
    GoogleApiClient googleClient;
    private Course currentCourse;
    private ArrayList<Hole> allHoles;
    private Integer currentHoleNum;
    private Hole currentHole;

    private Location currentFrontLocation;
    private Location currentMiddleLocation;
    private Location currentBackLocation;
//	private Location currentLocation;

    private TextView holeNumberView;
    private TextView frontView;
    private TextView middleView;
    private TextView backView;

    private boolean newPlacement = false;
    private PowerManager.WakeLock waitLock;

 //   LocationService locationService;
 //   boolean locationBound = false;

     // Wearable data layer constants
     public static final String WEARABLE_MESSAGE_HOLE_PATH = "/wearable_message/hole";
     public static final String WEARABLE_MESSAGE_COURSE_PATH = "/wearable_message/course";
     private static final String WEARABLE_DATA_PATH = "/wearable_data";

     MessageReceiver messageReceiver;
     IntentFilter messageFilter;

     private static final String TAG = "HandheldHoleActivity";

     private static final String KEY_IN_RESOLUTION = "is_in_resolution";
     private boolean mIsInResolution;
     /**
      * Request code for auto Google Play Services error resolution.
      */
     protected static final int REQUEST_CODE_RESOLUTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the views and register the hole placements for context menus
        setContentView(R.layout.activity_hole_detail);
        holeNumberView = (TextView) findViewById(R.id.holeNum);
        frontView = (TextView) findViewById(R.id.front);
        frontView.setOnLongClickListener(this);
//		registerForContextMenu(frontView);
        middleView = (TextView) findViewById(R.id.middle);
        middleView.setOnLongClickListener(this);
//		registerForContextMenu(middleView);
        backView = (TextView) findViewById(R.id.back);
        backView.setOnLongClickListener(this);
//		registerForContextMenu(backView);

        PowerManager pm = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        waitLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wlTag");
        waitLock.acquire();
        //Log.v("myTag", "Hole activity registering for local broadcasts.");
        // Setup for local broadcast receiver, handled in onResume and onPause
        messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new MessageReceiver();

        // Get the hole information, starting with hole 1
        currentCourse = getIntent().getParcelableExtra("course");
        allHoles = currentCourse.holeList;
        Log.v("myApp", "HoleActivity, onCreate, course: " + currentCourse.name);
        if (currentCourse.holeList != null)

        if (savedInstanceState != null && savedInstanceState.containsKey("currentHoleNum")) {
            // Use saved hole number
            currentHoleNum = savedInstanceState.getInt("currentHoleNum");
        } else {
            // Use hole 1 initially
            currentHoleNum = 1;

            // Save hole 1 to shared preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putString("hole",currentHoleNum.toString());
            prefEditor.commit();
        }

        holeNumberView.setText(currentHoleNum.toString());
        currentHole = allHoles.get(currentHoleNum - 1);
    //    setLocations(currentHole);
        //
        // Button handlers
        //

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle the Plus button
        final Button plusButton = (Button) findViewById(R.id.plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Use shared preferences to record new hole placements

                if (newPlacement) {

                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
//       			editor.clear();

                    String currentHoleString = currentHole.holeNum.toString();

                    String key = currentHoleString + ".front";
                    String value = " latitude: " + String.valueOf(currentFrontLocation.getLatitude()) + " longitude: " + String.valueOf(currentFrontLocation.getLongitude());
                    editor.putString(key, value);

                    key = currentHoleString + ".middle";
                    value = " latitude: " + String.valueOf(currentMiddleLocation.getLatitude()) + " longitude: " + String.valueOf(currentMiddleLocation.getLongitude());
                    editor.putString(key, value);

                    key = currentHoleString + ".back";
                    value = " latitude: " + String.valueOf(currentBackLocation.getLatitude()) + " longitude: " + String.valueOf(currentBackLocation.getLongitude());
                    editor.putString(key, value);

                    editor.commit();

                    Log.v("myApp", "SharedPreferences: " + sharedPref.getAll());

                    //       			editor.clear();
                    //       			editor.commit();

                    newPlacement = false;
                }

                if (currentHoleNum < (currentCourse.holes)) {
                    currentHoleNum++;
                    holeNumberView.setText(currentHoleNum.toString());
                    currentHole = allHoles.get(currentHoleNum - 1);
                    updateDisplay(LocationServices.FusedLocationApi.getLastLocation(googleClient));

                    // Send hole number to data later
                    new SendToDataLayerThread(HoleActivity.WEARABLE_MESSAGE_HOLE_PATH,currentHoleNum.toString()).start();

                    // Save hole number to shared preferences
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString("hole",currentHoleNum.toString());
                    prefEditor.commit();
                }
            }
        });

        // Handle the Minus button
        final Button minusButton = (Button) findViewById(R.id.minus);
        minusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentHoleNum > 1) {
                    currentHoleNum--;
                    holeNumberView.setText(currentHoleNum.toString());
                    currentHole = allHoles.get(currentHoleNum - 1);
                    updateDisplay(LocationServices.FusedLocationApi.getLastLocation(googleClient));

                    // Send hole number to data later
                    new SendToDataLayerThread(HoleActivity.WEARABLE_MESSAGE_HOLE_PATH,currentHoleNum.toString()).start();

                    // Save hole number to shared preferences
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString("hole",currentHoleNum.toString());
                    prefEditor.commit();
                }
            }
        });
    }

//*************************************************
// Lifecycle overrides
//*************************************************

    @Override
    protected void onStart() {
        super.onStart();

        // Build a new GoogleApiClient
        // the Wearable API is for communication over the Data Layer
        // and the Location Services provides current phone location
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Connect as Google client
        googleClient.connect();

        Log.v("myTag", "Hole activity onStart");


        // Start receiving local broadcasts
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
//         }
    }


   @Override
   protected void onPause() {
         // Unregister listeners
         LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
         super.onPause();
   }

    @Override
   protected void onResume() {
         // Register a local broadcast receiver, defined below.
         super.onResume();
         LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Pick up the current hole number, in case the handheld missed changes while asleep
        // Save hole number to shared preference for a persistent state

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedHoleNum = sharedPref.getString("hole", "");
        if (savedHoleNum != null) {
            Log.i(TAG, "Current hole number restored: " + savedHoleNum);
            currentHoleNum = Integer.parseInt(savedHoleNum);
            currentHole = allHoles.get(currentHoleNum - 1);
//          onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(googleClient));
        }

   }
     // Disconnect from Google Play Services when the Activity is no longer visible
   @Override
   protected void onStop() {
        if ((googleClient != null) && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
  }

     @Override
     protected void onDestroy(){
        Log.v("MyTag", "Hole activity onDestroy");

         // Stop receiving local broadcasts (unregister to avoid multiple registrations)
         LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
         super.onDestroy();
     }

      // ******************************
     // Google Connection callbacks
     // ******************************
     @Override
     public void onConnected(Bundle connectionHint) {

         // Register for location services

         // Create the LocationRequest object
         LocationRequest locationRequest = LocationRequest.create();
         // Use high accuracy
         locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
         // Set the update interval to 2 seconds
         locationRequest.setInterval(2);
         // Set the fastest update interval to 2 seconds
         locationRequest.setFastestInterval(2);
         // Set the minimum displacement
         locationRequest.setSmallestDisplacement(2);

         // Register listener using the LocationRequest object
         LocationServices.FusedLocationApi.requestLocationUpdates(googleClient, locationRequest, this);
     }

     @Override
     public void onConnectionSuspended(int cause) {
         retryConnecting();
     }

     @Override
     public void onConnectionFailed(ConnectionResult connectionResult) {

         if (!connectionResult.hasResolution()) {
             // Show a localized error dialog.
             GooglePlayServicesUtil.getErrorDialog(
                     connectionResult.getErrorCode(), this, 0, new DialogInterface.OnCancelListener() {
                         @Override
                         public void onCancel(DialogInterface dialog) {
                             retryConnecting();
                         }
                     }).show();
             return;
         }
         // If there is an existing resolution error being displayed or a resolution
         // activity has started before, do nothing and wait for resolution
         // progress to be completed.
         if (mIsInResolution) {
             return;
         }
         mIsInResolution = true;
         try {
             connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
         } catch (IntentSender.SendIntentException e) {
             retryConnecting();
         }
     }

     /**
      * Handle Google Play Services resolution callbacks.
      */
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         switch (requestCode) {
             case REQUEST_CODE_RESOLUTION:
                 retryConnecting();
                 break;
         }
     }

     private void retryConnecting() {
         mIsInResolution = false;
         if (!googleClient.isConnecting()) {
             googleClient.connect();
         }
     }

     /**
      * Google Play Location Service override
      */

     @Override
     public void onLocationChanged(Location location) {

         // Refresh the distances to hole placements
         Log.i(TAG, "Handheld location: " + location.toString() + "Accuracy: " + location.getAccuracy());
         if ((location != null) && (location.getAccuracy() < 25.0) && (location.getAccuracy() > 0.0)) {
             updateDisplay(location);
         }
     }

	// Handle long click, which saves current location as hole position
	@Override
	public boolean onLongClick(View placementView) {
		
		newPlacement = true;

//        Location currentLocation = locationService.getCurrentLocation();

        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleClient);
		
		if (placementView == frontView){
			Log.v("myApp", "OnLongClick method started.");
			currentFrontLocation = currentLocation;
		//	onLocationChanged(currentFrontLocation);
		}
		if (placementView == middleView){
			Log.v("myApp", "OnLongClick method started.");
			currentMiddleLocation = currentLocation;
		//	onLocationChanged(currentMiddleLocation);
		}
		if (placementView == backView){
			Log.v("myApp", "OnLongClick method started.");
			currentBackLocation = currentLocation;
		//	onLocationChanged(currentBackLocation);
		}
 //       locationService.setHole(currentHole);
		return false;		
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.v("myApp", "HoleActivity, onSavedInstanceState, currentHoleNum: " + currentHoleNum);
		savedInstanceState.putInt("currentHoleNum", currentHoleNum);
        // Google client resolution state
        savedInstanceState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
	}
	  
	//
	// Context menu handlers
	//
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                  ContextMenuInfo menuInfo) {
	      super.onCreateContextMenu(menu, v, menuInfo);
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.change_placement, menu);
	}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.set_placement:
//	              editNote(info.id);
                return true;
            case R.id.cancel:
//	              deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

     /**
      * Calculates distances to placements and updates the UI
      *
      * @param location Current device location
      */

     private void updateDisplay(Location location) {
         // float accuracy;
         // accuracy = location.getAccuracy();

         float conv = (float) 1.0936133;
         float yards = location.distanceTo(currentHole.getLocation("front")) * conv;
         String front = String.valueOf((int) yards);
         frontView.setText(front);

         yards = location.distanceTo(currentHole.getLocation("middle")) * conv;
         String middle = String.valueOf((int) yards);
         middleView.setText(middle);

         yards = location.distanceTo(currentHole.getLocation("back")) * conv;
         String back = String.valueOf((int) yards);
         backView.setText(back);

         // Keep the hole number display current
         holeNumberView.setText(currentHole.holeNum);
     }


     /* Define a local broadcast receiver
     * receives the new hole number
     * from the the DataLayerListener.
     */

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            String message = intent.getStringExtra("message");


            if (path.equals(WEARABLE_MESSAGE_HOLE_PATH)) {
                // The user swiped the wearable left or right
                // The message is the resultant hole number

                currentHoleNum = Integer.parseInt(message);
                currentHole = allHoles.get(currentHoleNum - 1);
                updateDisplay(LocationServices.FusedLocationApi.getLastLocation(googleClient));

            }
        }
    }
     /*
     * Utility class to send a message or data object to the wearable data layer.
     * Runs on a new thread to avoid blocking the UI thread.
     */

        class SendToDataLayerThread extends Thread {
            String path;
            String message;
            DataMap dataMap;

            // Create thread for sending messages to data layer
            SendToDataLayerThread(String p, String msg) {
                path = p;
                message = msg;
            }

            // Create thread for sending data objects to the data layer
            SendToDataLayerThread(String p, DataMap data) {
                path = p;
                dataMap = data;
            }

            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
                for (Node node : nodes.getNodes()) {
                    Log.v("myTag", "Location services next node discovered: " + node);
                    if (dataMap == null) {
                        MessageApi.SendMessageResult result =
                                Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                        if (result.getStatus().isSuccess()) {
                            Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                        } else {
                            // Log an error
                            Log.v("myTag", "ERROR: failed to send Message.");
                        }
                    } else {
                        PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                        putDMR.getDataMap().putAll(dataMap);
                        PutDataRequest request = putDMR.asPutDataRequest();
                        DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
                        if (result.getStatus().isSuccess()) {
                            Log.v("myTag", "DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                        } else {
                            // Log an error
                            Log.v("myTag", "ERROR: failed to send DataMap");
                        }

                    }

                }
            }
        }
 }





