package com.golfmarin.golf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.os.StrictMode;

import java.util.ArrayList;
import java.util.Collections;

//import android.util.Log;
import android.util.Log;
import android.view.MenuItem;


public class CourseListActivity extends FragmentActivity implements CourseListFragment.Callbacks {
	
	County selectedCounty = null;
	ArrayList<Course> allCourses = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_list);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Get the intent info
		selectedCounty = getIntent().getParcelableExtra("county");
		allCourses = getIntent().getParcelableArrayListExtra("courses");
		
		
		// Add a course list fragment for the selected county
		
		// Start by making a list of courses in the selected county
		ArrayList<Course> filteredCourses = new ArrayList<Course>();
		Course course;
		int i = 0;
        while (i < allCourses.size()) {
        	course = allCourses.get(i);
        	if (course.county.equalsIgnoreCase(selectedCounty.name)) {
	           filteredCourses.add(course);		        	
        	}
        	i++;
        }

		// Order the courses list
		Collections.sort(filteredCourses);

        // Then replace the course list fragment with one containing courses for the selected county
		FragmentManager fm = getSupportFragmentManager();
    	CourseListFragment gcf = (CourseListFragment) fm.findFragmentByTag("CourseList");
    	if ( gcf == null) {
    		gcf = new CourseListFragment();
        	Bundle arguments = new Bundle();
        	arguments.putParcelableArrayList("courses", filteredCourses);
        	gcf.setArguments(arguments);           	
    		//Log.v("myApp", "Course List Activity: Create a new Course List Fragment " + gcf);
    		FragmentTransaction ft = fm.beginTransaction();
        	ft.replace(R.id.course_list_container, gcf, "CourseList");
        	//ft.addToBackStack(null);
        	ft.commit();
    	}
    	else {
    		//Log.v("myApp", "Course List Activity: Use existing course List Fragment " + gcf);
    	}
	}
	
public void onCourseSelected(Course c) {

			// Start the detail activity for the selected course.
			Intent detailIntent = new Intent(this, CourseDetailActivity.class);
			detailIntent.putExtra("county", selectedCounty);
			detailIntent.putExtra("course", c);
            detailIntent.putExtra("courses", allCourses);
			startActivity(detailIntent);
		}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this,
					CountyListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}