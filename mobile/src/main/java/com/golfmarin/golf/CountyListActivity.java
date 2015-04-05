package com.golfmarin.golf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import java.util.ArrayList;

import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.os.StrictMode;




//import android.util.Log;

/**
 * An activity representing a list of Counties. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link CountyDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link CountyListFragment} and the item details (if present) is a
 * {@link CountyDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link CountyListFragment.Callbacks} interface to listen for item selections.
 */
public class CountyListActivity extends FragmentActivity implements
		CountyListFragment.Callbacks,CourseListFragment.Callbacks, Weather.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private ArrayList<County> counties;
	private ArrayList<Course> courses;
	WebViewFragment webViewFragment = null;
	WebView webView = null;
	Weather parser = null;
	String localWeather = new String("Getting Weather.");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()
        .penaltyLog()
        .build());
		super.onCreate(savedInstanceState);
		

		DataModel dm = new DataModel(this);
		counties = dm.getCounties();
		courses = dm.getCourses();

		
		
		setContentView(R.layout.activity_county_list);
		this.setTitle(R.string.title_county_list);
		
		// Set the header bar, if on tablet
		TextView header = (TextView) findViewById(R.id.county_list_header);
		if (header != null)
		header.setText(R.string.county_list);
		
		FragmentManager fm = getSupportFragmentManager();

		if (findViewById(R.id.detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
			
            CountyDetailFragment df = (CountyDetailFragment) fm.findFragmentByTag("Detail");
            if (df == null) {
	            // Initialize new detail fragment
	            //Log.v("myApp", "List Activity: Initialize new detail view");
	            df = new CountyDetailFragment();
	            Bundle args = new Bundle();
	            // args.putParcelable("county", new County("Marin"));
	            args.putParcelable("county", counties.get(0));
	            args.putParcelableArrayList("courses", courses);
	            df.setArguments(args);
	            fm.beginTransaction().replace(R.id.detail_container, df, "Detail").commit();
            }  
        	else {
        		//Log.v("myApp", "List Activity, Use existing Detail Fragment " + df);	
        	}
            
            // Work on the web view
             webViewFragment = (WebViewFragment) fm.findFragmentById(R.id.web);
             webView = webViewFragment.getWebView();            
             if (parser == null) parser = new Weather(this);
             parser.getWeather(counties.get(0));             
		}
         // Initialize the county list fragment            
        	CountyListFragment cf = (CountyListFragment) fm.findFragmentByTag("List");
        	if ( cf == null) {
        		cf = new CountyListFragment();
            	Bundle arguments = new Bundle();
            	arguments.putParcelableArrayList("counties", counties);
            	cf.setArguments(arguments);           	
        		//Log.v("myApp", "CountyListActivity: Create a new CountyListFragment " + cf);
        		FragmentTransaction ft = fm.beginTransaction();
            	ft.replace(R.id.county_list, cf, "List");
            	//ft.addToBackStack(null);
            	ft.commit();
            	//fm.beginTransaction().replace(R.id.county_list, cf, "List").commit();
        	}
        	else {
        		//Log.v("myApp", "List Activity: Use existing List Fragment " + cf);
        	}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link CountyListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onCountySelected(County county) {
		
		
		if (mTwoPane) {
			
			// Change the header bar
			TextView header = (TextView) findViewById(R.id.county_list_header);
			header.setText(R.string.course_list);
			
			// Replace county list fragment with course list fragment
			// for the selected county
			
			// Start by making a list of courses in the selected county
			ArrayList<Course> filteredCourses = new ArrayList<Course>();
			Course course;
			int i = 0;
	        while (i < courses.size()) {
	        	course = courses.get(i);
	        	if (course.county.equalsIgnoreCase(county.name)) {
		           filteredCourses.add(course);		        	
	        	}
	        	i++; 
	        }
	        
	        // Then replace the county list fragment with the course list fragment
			FragmentManager fm = getSupportFragmentManager();
        	CourseListFragment gcf = (CourseListFragment) fm.findFragmentByTag("CourseList");
        	if ( gcf == null) {
        		gcf = new CourseListFragment();
            	Bundle arguments = new Bundle();
            	arguments.putParcelableArrayList("courses", filteredCourses);
            	gcf.setArguments(arguments);           	
        		//Log.v("myApp", "List Activity: Create a new Course List Fragment " + gcf);
        		FragmentTransaction ft = fm.beginTransaction();
            	ft.replace(R.id.county_list, gcf, "CourseList");
            	ft.addToBackStack(null);
            	ft.commit();
        	}
        	else {
        		//Log.v("myApp", "List Activity: Use existing course List Fragment " + gcf);
        	}
			
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putParcelable("county", county);
            arguments.putParcelableArrayList("courses", courses);
			CountyDetailFragment fragment = new CountyDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.detail_container, fragment).commit();
			
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((CountyListFragment) getSupportFragmentManager().findFragmentById(
					R.id.county_list)).setActivateOnItemClick(true);
			
            parser.getWeather(county);

		} else {
			// In single-pane mode, simply start the course list activity
			// for the selected county.
			Intent detailIntent = new Intent(this, CourseListActivity.class);
			detailIntent.putExtra("county", county);
            detailIntent.putExtra("courses", courses);
			startActivity(detailIntent);
		}
	}
	public void onCourseSelected(Course c) {
		
		// Get the county object for the selected course
		County selectedCounty = null;
		int i = 0;
        while (i < counties.size()) {
        	if (c.county.equalsIgnoreCase(counties.get(i).name)) {
	           selectedCounty = counties.get(i);		        	
        	}
        	i++;
        }
		
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// replacing the detail fragment using a fragment transaction.
			// In this case the detail is updated to show the selected golfcourse			
			
			// Display the course detail fragment
			Bundle arguments = new Bundle();
			arguments.putParcelable("county", selectedCounty);
			arguments.putParcelable("course",c);
            arguments.putParcelableArrayList("courses", courses);
			CourseDetailFragment fragment = new CourseDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.detail_container, fragment).commit();
			
		} 
		
	}

	public void onBackPressed(){
		super.onBackPressed();
		// Set the header bar
		TextView header = (TextView) findViewById(R.id.county_list_header);
		if (header != null)
		header.setText(R.string.county_list);
	}
	
	public void onLocalWeatherReady(String weather){
        webView.clearView();
        webView.loadData(weather, "text/html", null);
		
	}

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
