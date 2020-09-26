package com.dot.maps;

import com.google.maps.*;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.PlaceDetails;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailsFactory {
    private final GeoApiContext context;
    private final PlaceAutocompleteRequest.SessionToken session;

    public PlaceDetailsFactory(GeoApiContext context, PlaceAutocompleteRequest.SessionToken session) {
        this.context = context;
        this.session = session;
    }

    public void request(String userInput, PendingResult.Callback<List<PlaceDetails>> callback) {
        PlacesApi.placeAutocomplete(context, userInput, session)
                .setCallback(new PendingResult.Callback<AutocompletePrediction[]>() {
                    @Override
                    public void onResult(AutocompletePrediction[] result) {
                        List<PlaceDetails> details = new ArrayList<>();

                        for (AutocompletePrediction prediction : result)
                            requestSingleDetails(prediction, detailsCallback(details, callback));

                        callback.onResult(details);
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

    private PendingResult.Callback<PlaceDetails> detailsCallback(List<PlaceDetails> details, PendingResult.Callback<List<PlaceDetails>> callback) {
        return new PendingResult.Callback<PlaceDetails>() {
            @Override
            public void onResult(PlaceDetails result) {
                details.add(result);
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        };
    }
}
