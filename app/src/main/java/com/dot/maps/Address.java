package com.dot.maps;

import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;

public class Address {
    private final String poiName;
    private final String streetName;
    private final String streetNumber;
    private final String municipality;

    public Address(FuzzySearchDetails searchDetails) {
        this(searchDetails.getPoi().getName(), searchDetails.getAddress().getStreetName(), searchDetails.getAddress().getStreetNumber(), searchDetails.getAddress().getMunicipalitySubdivision());
    }

    public Address(String poiName, String streetName, String streetNumber, String municipality) {
        this.poiName = poiName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.municipality = municipality;
    }

    public String getPoiName() {
        return poiName;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getMunicipality() {
        return municipality;
    }

    public boolean isRegularAddress() {
        return poiName.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;

        return equals(address);
    }

    public boolean equals(Address other) {
        return poiName.equals(other.poiName) &&
                streetName.equals(other.streetName) &&
                streetNumber.equals(other.streetNumber) &&
                municipality.equals(other.municipality);
    }

    public boolean contains(Address other) {
        return municipality.equals(other.municipality) &&
                (poiName.contains(other.poiName) ||
                other.poiName.contains(poiName) ||
                streetName.contains(other.streetName) ||
                other.streetName.contains(streetName));
    }
}
