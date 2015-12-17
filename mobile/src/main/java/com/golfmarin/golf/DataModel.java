package com.golfmarin.golf;

import java.util.ArrayList;
import org.json.JSONArray;
import android.content.Context;
import android.util.Log;


public class DataModel {
	
	// Define a class to handle the custom data
	// Initially, just reads json-formatted files and returns an array of objects
   
		ArrayList<Region> regionsArray = new ArrayList<Region>();
    	ArrayList<Course> allCoursesArray = new ArrayList<Course>();
        ArrayList<Course> coursesArray = new ArrayList<Course>();
        ArrayList<County> countiesArray = new ArrayList<County>();
		
        // Initializer to read a text file into an array of golfcourse objects    
		public DataModel(Context ctx) {
	        // Read counties from jason file
	        JsonParser jp = new JsonParser();
	        JSONArray ja1 = jp.getJSONFromFile(ctx, "counties-json.txt", "counties", "county");
	        countiesArray = jp.getCountiesFromJSON(ja1);

            // Read Regions from json file
            ja1 = jp.getJSONFromFile(ctx, "regions-json.txt", "regions", "region");
            regionsArray = jp.getRegionsFromJSON(ja1);
	        
	    	// Read Sonoma courses from file
	    	JSONArray ja = jp.getJSONFromFile(ctx, "sonoma-json.txt", "golfCourses", "golfCourse");
	    	coursesArray = jp.getCoursesFromJSON(ja, "Sonoma");
	    	allCoursesArray.addAll(coursesArray);
	    	
	    	// Read Marin courses from file
	    	ja = jp.getJSONFromFile(ctx, "marin-json.txt", "golfCourses", "golfCourse");
	    	coursesArray = jp.getCoursesFromJSON(ja, "Marin");
	    	allCoursesArray.addAll(coursesArray);
	    	
	    	// Read San Francisco courses from file
	    	ja = jp.getJSONFromFile(ctx, "SanFrancisco-json.txt", "golfCourses", "golfCourse");
	    	coursesArray = jp.getCoursesFromJSON(ja, "San Francisco");
	    	allCoursesArray.addAll(coursesArray);
	    	
	    	// Read San Mateo courses from file
	    	ja = jp.getJSONFromFile(ctx, "SanMateo-json.txt", "golfCourses", "golfCourse");
	    	coursesArray = jp.getCoursesFromJSON(ja, "San Mateo");
	    	allCoursesArray.addAll(coursesArray);	
	    	
	    	// Read Napa courses from file
	    	ja = jp.getJSONFromFile(ctx, "napa-json.txt", "golfCourses", "golfCourse");
	    	coursesArray = jp.getCoursesFromJSON(ja, "Napa");
	    	allCoursesArray.addAll(coursesArray);
	    	
	    	// Read Contra Costa info from file
	    	ja = jp.getJSONFromFile(ctx, "ContraCosta-json.txt", "golfCourses", "golfCourse");
	    	coursesArray = jp.getCoursesFromJSON(ja, "Contra Costa");
	    	allCoursesArray.addAll(coursesArray);
	    	
	    	// Read Solano courses from file
	    	ja = jp.getJSONFromFile(ctx, "solano-json.txt", "golfCourses", "golfCourse");
	    	coursesArray = jp.getCoursesFromJSON(ja, "Solano");
	    	allCoursesArray.addAll(coursesArray);

            /* Consolidate counties into regions
            *
            * WineCountry (Marin, Sonoma, Napa)
            * SanFrancisco (San Francisco)
            * Peninsula (San Mateo, Santa Clara)
            * EastBay (Solano, Contra Costa)
             */
            for (Region region: regionsArray) {
                if (region.name.equals("Wine Country")) {

                    for (Course course: allCoursesArray) {
                        if(course.county.equals("Marin") |
                                course.county.equals("Sonoma") |
                                course.county.equals("Napa")) {
                            course.region=region.name;
                            region.courses.add(course);
                        }
                    }
                }
                if (region.name.equals("San Francisco")) {

                    for (Course course: allCoursesArray) {
                        if(course.county.equals("San Francisco")) {
                            course.region=region.name;
                            region.courses.add(course);
                        }
                    }
                }
                if (region.name.equals("Silicon Valley")) {

                    for (Course course: allCoursesArray) {
                        if(course.county.equals("San Mateo") |
                                course.county.equals("Santa Clara")) {
                            course.region=region.name;
                            region.courses.add(course);
                        }
                    }
                }
                if (region.name.equals("East Bay")) {

                    for (Course course: allCoursesArray) {
                        if(course.county.equals("Solano") |
                                course.county.equals("Contra Costa")) {
                            course.region=region.name;
                            region.courses.add(course);
                        }
                    }
                }
            }
		}

		// Method to retrieve regions
		public ArrayList<Region> getRegions() {
			return regionsArray;
		}

		// Method to retrieve counties
	    public ArrayList<County> getCounties() {
	    	return countiesArray;
	    }
	    
		// Method to retrieve courses
	    public ArrayList<Course> getCourses() {
	    	return allCoursesArray;
	    }
}


