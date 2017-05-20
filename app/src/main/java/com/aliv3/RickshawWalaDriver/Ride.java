package com.aliv3.RickshawWalaDriver;

public class Ride {

    public Integer id;
    public String clientName;
    public double originLat;
    public double originLong;
    public double destLat;
    public double destLong;
    public double fare;

    public Ride(Integer id, String clientName, double originLat, double originLong, double destLat, double destLong, double fare) {
        this.id = id;
        this.clientName = clientName;
        this.originLat = originLat;
        this.originLong = originLong;
        this.destLat = destLat;
        this.destLong = destLong;
        this.fare = fare;
    }
}
