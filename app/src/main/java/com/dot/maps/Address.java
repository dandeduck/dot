package com.dot.maps;

import com.google.maps.model.AddressComponent;
import com.google.maps.model.PlaceDetails;

import java.util.HashMap;
import java.util.Map;

public class Address {
    private final Map<String, String> shortNameMap;

    public Address(PlaceDetails details) {
        shortNameMap = new HashMap<>();
        fillMap(details);
    }

    private void fillMap(PlaceDetails details) {
        for (AddressComponent component : details.addressComponents)
            shortNameMap.put(component.types[0].name(), component.shortName);
    }

    public String get(String type) {
        return shortNameMap.get(type);
    }
}
