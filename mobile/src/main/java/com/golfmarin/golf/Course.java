package com.golfmarin.golf;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Course implements Parcelable, Comparable<Course> {
	
	String	name = "None";
	String	address = "None";
	String	city = "None";
	String	county = "None";
	String	courseInfo = "None";
	String	directions = "None";
	int   	holes;
	boolean	isPublic;
	String	imageURL = "None";
	String	id = "None";
	String	info = "None";
	double	latitude = 0.0;
    double	longitude = 0.0;
	String  phone = "None";
	String  slope = "None";
	String  thumbnailURL = "sanmateo";
	String  woeid = "None";
	
//	HoleArrayList holeList = new HoleArrayList();
//	Hole[] holeArray = new Hole[36];

	ArrayList<Hole> holeList = new ArrayList<Hole>();

	
	Course(String name) {
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
        out.writeString(address);
        out.writeString(city);
        out.writeString(county);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(phone);
        out.writeInt(holes);
        out.writeByte((byte) (isPublic ? 1 : 0));
        
//        out.writeParcelableArray(holeArray, 0);

        out.writeTypedList(holeList);
        
    }

    public static final Parcelable.Creator<Course> CREATOR
            = new Parcelable.Creator<Course>() {
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
    
    private Course(Parcel in) {
        name = in.readString();
        address = in.readString();
        city = in.readString();
        county = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        phone = in.readString();
        holes = in.readInt();
        isPublic = in.readByte() ==1;
        
//        holeArray = (Hole[]) in.readParcelableArray(Hole.class.getClassLoader());
//        holeList = in.readTypedList(list, c)

        in.readTypedList(holeList, Hole.CREATOR);
        
//        Log.v("myApp", "Course " + name + ", holeList: " + holeList);
    }

    // This makes Course objects comparable, so a course list can easily be sorted as a Collection
    public int compareTo(Course c) {
        Course compareToCourse = c;
        return this.name.compareTo(compareToCourse.name);
    }
}
