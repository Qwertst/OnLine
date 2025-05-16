package ru.hse.online.GroupService.data;

public class Location {
    public final double lat;
    public final double lng;

    public Location() {
        this.lat = 0.0;
        this.lng = 0.0;
    }

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String toJson() {
        return "{\"lat\": " + lat + ", \"lng\": " + lng + "}";
    }
}
