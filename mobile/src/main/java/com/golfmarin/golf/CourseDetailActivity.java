package com.golfmarin.golf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class CourseDetailActivity extends FragmentActivity implements
  GoogleApiClient.ConnectionCallbacks,
  OnConnectionFailedListener
{


    private static final String WEARABLE_START_PATH = "/wearable_start";

    GoogleApiClient googleClient;
	
	Course displayedCourse = null;

    // Check screen size onStart
    // Create a google API client for starting the wearable app
    @Override
    protected void onStart() {
        super.onStart();
        // Get the screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        size.x = display.getWidth();
        size.y = display.getHeight();;
        int width = size.x;
        int height = size.y;
        Log.v("myApp", "Height: " + height);

        // Build a new GoogleApiClient
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();

    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Context ctx = this;
		displayedCourse = getIntent().getParcelableExtra("course");
	    
		setContentView(R.layout.activity_course_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.

			Bundle arguments = new Bundle();
            arguments.putParcelable("county", getIntent().getParcelableExtra("county"));
            arguments.putParcelable("course", getIntent().getParcelableExtra("course"));
            arguments.putParcelableArrayList("courses",getIntent().getParcelableArrayListExtra("courses"));
			CourseDetailFragment fragment = new CourseDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.detail_container, fragment).commit();
		}
			
	// Handle the Play button
    final Button button = (Button) findViewById(R.id.play);
    button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	if (displayedCourse.holeList.size() != 0) {

				// Start the hole activity for the displayed course.
				Intent playIntent = new Intent(ctx, HoleActivity.class);
				playIntent.putExtra("course", displayedCourse);
				startActivity(playIntent);

                // Tell wearable to create a notification that
                // the user can click to start the wearable golf app
                DataMap notifyWearable = new DataMap();
                notifyWearable.putString("title", "Play Golf");
                notifyWearable.putString("body", "Start now?");
                notifyWearable.putString("course", displayedCourse.name);
                notifyWearable.putLong("time", new Date().getTime());

                // Send to data layer
                if (googleClient.isConnected())
                new SendToDataLayerThread(WEARABLE_START_PATH, notifyWearable).start();
        	}
        }
       });    
	}

    // Placeholders for required connection callbacks

    @Override
    public void onConnected(Bundle b) { }


    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }


    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			Intent intent = new Intent(this, CountyListActivity.class);
			intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return(true);
		}
		return super.onOptionsItemSelected(item);
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
