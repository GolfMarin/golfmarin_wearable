package com.golfmarin.golf;

import android.os.Parcelable;
import android.os.Parcel;

public class County implements Parcelable {
	
	String id = "0";
	String name = "None";
	String countyInfo = "None";
	double latitude = 0.0;
	double longitude = 0.0;
	String woeid = "0";
	String thumbnailURL = "sanmateo";
	
	County(String name) {
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
    }

    public static final Parcelable.Creator<County> CREATOR
            = new Parcelable.Creator<County>() {
        public County createFromParcel(Parcel in) {
            return new County(in);
        }

        public County[] newArray(int size) {
            return new County[size];
        }
    };
    
    private County(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        woeid = in.readString();
    }
	

}
