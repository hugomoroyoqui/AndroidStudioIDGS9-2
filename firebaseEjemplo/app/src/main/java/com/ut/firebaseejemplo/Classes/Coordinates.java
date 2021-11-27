package com.ut.firebaseejemplo.Classes;

import java.util.Date;

public class Coordinates {
    String id;
    Date date;
    Float latitude;
    Float longitude;

    public Coordinates(String id, Date date, Float latitude, Float longitude) {
        this.id = id;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}
