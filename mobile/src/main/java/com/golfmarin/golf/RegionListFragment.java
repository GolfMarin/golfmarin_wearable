package com.golfmarin.golf;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import android.util.Log;

/**
 * A list fragment representing a list of golf regions. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link RegionDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RegionListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
    private ArrayList<Region> regions = new ArrayList<Region>();

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onRegionSelected(Region c);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onRegionSelected(Region c) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RegionListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        if(getArguments() != null && getArguments().containsKey("regions")) {

        	regions = getArguments().getParcelableArrayList("regions");
        }
 
/*      setListAdapter(new ArrayAdapter<Region>(getActivity(),
        		R.layout.simple_list_item_activated_1,
                R.id.text1,
                counties));
*/
        setListAdapter(new RegionArrayAdapter(getActivity(), R.layout.regionlistitem));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onRegionSelected((Region) listView.getItemAtPosition(position));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	/*
	 * ARRAY ADAPTER SECTION
	 */
    
    // Customize an array adapter for counties
    public class RegionArrayAdapter extends ArrayAdapter<Region> {
    	private final Context context;
    	private final int regionlistitem;

    	public RegionArrayAdapter(Context context, int listitem) {
    		
    		super(context, listitem, regions);
    		this.context = context;
    		this.regionlistitem = listitem;
    	}
    		
    	public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = (View) LayoutInflater.from(context).inflate(
                            regionlistitem, parent, false);
                }       
            ImageView icon = (ImageView) v.findViewById(R.id.region_image);
    	    TextView line1 = (TextView) v.findViewById(R.id.region_name);
    	    TextView line2 = (TextView) v.findViewById(R.id.region_description);

    	    if (regions != null) {
	    	    icon.setImageResource(getResources().getIdentifier(regions.get(position).thumbnailURL,
	    	    			"drawable", "com.golfmarin.golf"));

	    		line1.setText(regions.get(position).name);
	    		line2.setText(regions.get(position).regionInfo);
				line2.setText("");
    	    }
    	    	v.setBackgroundColor(0xFFFFFFFF);
                return v;
            }	
    	}
}


