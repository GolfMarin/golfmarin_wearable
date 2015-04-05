package com.golfmarin.golf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;

import android.content.ActivityNotFoundException;

/**
 * A fragment representing a single County detail screen. This fragment is
 * either contained in a {@link CountyListActivity} in two-pane mode (on
 * tablets) or a {@link CountyDetailActivity} on handsets.
 */
public class CourseDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	  private County selectedCounty;
	  private Course selectedCourse;
	  private ArrayList<Course> courses;
	  
	    GoogleMap mMap;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CourseDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey("county") && getArguments().containsKey("courses")) {
		
  //      if (getArguments().containsKey("county")) {
        	selectedCounty = getArguments().getParcelable("county");
            selectedCourse = getArguments().getParcelable("course");
            courses = getArguments().getParcelableArrayList("courses");
            
            Log.v("myApp", "Course detail, onCreate. Course:" + selectedCourse.name + "Holes: " + selectedCourse.holeList);
        }
        
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_course_detail,
				container, false);
		
		// Handle callback for dialing the phone number, when selected
		TextView phoneNumber = (TextView) rootView.findViewById(R.id.course_phone);
		phoneNumber.setOnClickListener(new OnClickListener() {
			public void onClick(View phoneView) {
				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + selectedCourse.phone));				
					startActivity(callIntent);
					
				} catch (ActivityNotFoundException e) {
					//Log.e("myApp", "Call failed", e);					
				}
			}			
		});
		
		// ((TextView) rootView.findViewById(R.id.course_detail)).setBackgroundColor(0xFFFFFFFF);

		// Show the  content as text in a table of  TextView's.
		if (selectedCourse != null) {
			((TextView) rootView.findViewById(R.id.course_name)).setText(selectedCourse.name);
			((TextView) rootView.findViewById(R.id.course_address)).setText(selectedCourse.address);
			((TextView) rootView.findViewById(R.id.course_city)).setText(selectedCourse.city);
			if (selectedCourse.isPublic == true){
			  ((TextView) rootView.findViewById(R.id.course_description)).setText(selectedCourse.holes + "-hole public course");
			} else {
				((TextView) rootView.findViewById(R.id.course_description)).setText(selectedCourse.holes + "-hole private course");
			}
			    ((TextView) rootView.findViewById(R.id.course_phone)).setText(selectedCourse.phone);
			
        // Try to obtain a map reference from the layout, and add a marker.
		// TODO: put this in its own fragment
		        mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(selectedCounty.latitude,selectedCounty.longitude)).zoom(10).build();
		        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); 
		        
		        // Add markers for all the golfcourses
		        mMap.clear();
		        int i=0;
		        Course c;
		        while (i < courses.size()) {
		        	c = courses.get(i);
		    		// Log.v("myApp", "CountyDetailFragment, course.county: " + c.county);
		        	if (c.county.equalsIgnoreCase(selectedCounty.name)) {
		        		if (c.name.equalsIgnoreCase(selectedCourse.name)) {
				            mMap.addMarker(new MarkerOptions().position(new LatLng(c.latitude, c.longitude)).title(c.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow();
					        cameraPosition = new CameraPosition.Builder().target(new LatLng(selectedCourse.latitude,selectedCourse.longitude)).zoom(10).build();
					        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		        		}
		        		else {
			            mMap.addMarker(new MarkerOptions().position(new LatLng(c.latitude, c.longitude)).title(c.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		        		}
			        //	Log.v("myApp", "CountyDetailFragment,Course: " + c.name + " latitude: " + c.latitude + " longitude: " + c.longitude);
		        	}
		        	i++; 
		        } 
		}

		return rootView; 
	}
  
}
