package com.dot.maps;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;

public class Place {
    private final Address address;
    private final LatLng location;

    public Place(FuzzySearchDetails details) {
        this(new Address(details), details.getPosition());
    }

    public Place(Address address, LatLng location) {
        this.address = address;
        this.location = location;
    }

    public Address getAddress() {
        return address;
    }

    public LatLng getLocation() {
        return location;
    }
}
