package com.dot.maps;

import com.google.maps.*;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.PlaceDetails;

import java.util.ArrayList;
import java.util.List;

public class PlaceFactory {
    private final GeoApiContext context;
    private final PlaceAutocompleteRequest.SessionToken session;

    public PlaceFactory(GeoApiContext context, PlaceAutocompleteRequest.SessionToken session) {
        this.context = context;
        this.session = session;
    }

    public void request(String userInput, PendingResult.Callback<List<Place>> callback) {
        PlacesApi.placeAutocomplete(context, userInput, session)
                .setCallback(new PendingResult.Callback<AutocompletePrediction[]>() {
                    @Override
                    public void onResult(AutocompletePrediction[] result) {
                        List<Place> places = new ArrayList<>();

                        for (AutocompletePrediction prediction : result)
                            requestSingleDetails(prediction, placeCallback(places, callback));

                        callback.onResult(places);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        callback.onFailure(e);
                    }
                });
    }

    private void requestSingleDetails(AutocompletePrediction prediction, PendingResult.Callback<PlaceDetails> callback) {
        PlacesApi.placeDetails(context, prediction.placeId, session)
                .fields(PlaceDetailsRequest.FieldMask.ADDRESS_COMPONENT,
                        PlaceDetailsRequest.FieldMask.GEOMETRY_LOCATION_LAT,
                        PlaceDetailsRequest.FieldMask.GEOMETRY_LOCATION_LNG)
                .setCallback(callback);
    }

    private PendingResult.Callback<PlaceDetails> placeCallback(List<Place> details, PendingResult.Callback<List<Place>> callback) {
        return new PendingResult.Callback<PlaceDetails>() {
            @Override
            public void onResult(PlaceDetails result) {
                details.add(new Place(result));
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        };
    }
}
