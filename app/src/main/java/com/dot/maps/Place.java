package com.dot.maps;

import com.google.maps.model.PlaceDetails;
import com.tomtom.online.sdk.common.location.LatLng;

public class Place {
    private final Address address;
    private final LatLng location;

    public Place(PlaceDetails details) {
        this(new Address(details), new LatLng(details.geometry.location.lat, details.geometry.location.lng));
    }

    public Place(Address address, LatLng location) {
        this.address = address;
        this.location = location;
    }
}
