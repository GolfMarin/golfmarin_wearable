package com.golfmarin.golf;

import android.os.Parcelable;
import android.os.Parcel;

import java.util.ArrayList;

public class Region implements Parcelable {
	
	String id = "0";
	String name = "None";
	String regionInfo = "None";
	double latitude = 0.0;
	double longitude = 0.0;
	String woeid = "0";
	String thumbnailURL = "sanmateo";
    ArrayList<Course> courses = new ArrayList<Course>();
	
	Region(String name) {
		this.name = name;
	}
	
	public String toString () {
		return name;
	}
	
	// Parcelable implementation
	
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(woeid);
        out.writeTypedList(courses);
    }

    public static final Parcelable.Creator<Region> CREATOR
            = new Parcelable.Creator<Region>() {
        public Region createFromParcel(Parcel in) {
            return new Region(in);
        }

        public Region[] newArray(int size) {
            return new Region[size];
        }
    };
    
    private Region(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        woeid = in.readString();
        in.readTypedList(courses, Course.CREATOR);
    }
	

}
