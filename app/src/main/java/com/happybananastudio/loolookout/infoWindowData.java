package com.happybananastudio.loolookout;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mgint on 2/9/2018.
 */

public class infoWindowData {

    private LatLng latlng;
    private String gender;
    private String size;
    private String clean;
    private String traffic;
    private String access;
    private String closing;
    private String amenities;
    private int amenityCount;
    private int voteCount;
    private String lastDate;

    // Setters
    public void setLatLng( LatLng newLatLng ){
        latlng = newLatLng;
    }
    public void setGender( String newGender ){
        gender = newGender;
    }
    public void setSize( String newSize ){
        size = newSize;
    }
    public void setClean( String newClean ){
        clean = newClean;
    }
    public void setTraffic( String newGender ){
        traffic = newGender;
    }
    public void setAccess( String newAccess ){
        access = newAccess;
    }
    public void setClosing( String newClosing ){
        closing = newClosing;
    }
    public void setAmenities( String newAmenities ){
        amenities = newAmenities;
    }
    public void setAmenityCount( int count ) { amenityCount = count; }
    public void setVoteCount( int count ) { voteCount = count; }
    public void setLastDate( String date ){ lastDate = date; }

    // Getters
    public LatLng getLatLng( ){
        return latlng;
    }
    public String getGender(){
        return gender;
    }
    public String getSize(){
        return size;
    }
    public String getClean(){
        return clean;
    }
    public String getTraffic(){
        return traffic;
    }
    public String getAccess(){
        return access;
    }
    public String getClosing(){
        return closing;
    }
    public String getAmenities(){
        return amenities;
    }
    public int getAmenityCount(){
        return amenityCount;
    }
    public int getVoteCount(){
        return voteCount;
    }
    public String getLastDate(){ return lastDate; }
}
