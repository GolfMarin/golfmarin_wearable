package com.golfmarin.golf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

//import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
// import com.google.android.maps.MapController;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;

/**
 * A fragment representing a single County detail screen. This fragment is
 * either contained in a {@link CountyListActivity} in two-pane mode (on
 * tablets) or a {@link RegionDetailActivity} on handsets.
 */
public class RegionDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Region selectedRegion;
    // private Course selectedCourse;
    private ArrayList<Course> courses;

    GoogleMap mMap;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RegionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    if(getArguments() != null && getArguments().containsKey("region") && getArguments().containsKey("courses")) {


        selectedRegion = getArguments().getParcelable("region");
        // selectedCourse = getArguments().getParcelable("course");
        //       courses = getArguments().getParcelableArrayList("courses");

        courses = selectedRegion.courses;
        //    }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_region_detail,
                container, false);

        ((TextView) rootView.findViewById(R.id.region_detail)).setBackgroundColor(0xFFFFFFFF);

        // Show the  content as text in a TextView.
        if (selectedRegion != null) {
            ((TextView) rootView.findViewById(R.id.region_detail))
                    .setText(selectedRegion.name + " Region \n\n");

            // Try to obtain a map reference from the layout, and add a marker.
            // TODO: put this in its own fragment
            MapsInitializer.initialize(getActivity());
            mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(selectedRegion.latitude, selectedRegion.longitude)).zoom(10).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // Add markers for all the golfcourses
            mMap.clear();
            int i = 0;
            Course c;
            while (i < courses.size()) {
                c = courses.get(i);
                // Log.v("myApp", "CountyDetailFragment, course.county: " + c.county);
                if (c.region.equalsIgnoreCase(selectedRegion.name)) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(c.latitude, c.longitude)).title(c.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    //	Log.v("myApp", "CountyDetailFragment,Course: " + c.name + " latitude: " + c.latitude + " longitude: " + c.longitude);
                }
                i++;
            }
        }

            return rootView;
        }
    }




