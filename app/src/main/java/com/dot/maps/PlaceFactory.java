package com.dot.maps;

import android.content.Context;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.location.LatLngBias;
import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;
import com.tomtom.online.sdk.search.SearchException;
import com.tomtom.online.sdk.search.fuzzy.FuzzyLocationDescriptor;
import com.tomtom.online.sdk.search.fuzzy.FuzzyOutcome;
import com.tomtom.online.sdk.search.fuzzy.FuzzyOutcomeCallback;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchDetails;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchEngineDescriptor;
import com.tomtom.online.sdk.search.fuzzy.FuzzySearchSpecification;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class PlaceFactory implements Closeable {
    private final SearchApi searchApi;
    private final FuzzySearchEngineDescriptor searchEngineDescriptor;
    private final double searchRadius;

    public PlaceFactory(Context context, String apiKey, int maxResults, double searchRadius) {
        this.searchRadius = searchRadius;

        searchApi = OnlineSearchApi.create(context, apiKey);
        searchEngineDescriptor = searchEngineDescriptor(maxResults);
    }

    public void request(LatLng searchLocation, String userInput, Callback<List<Place>> callback) {
        FuzzySearchSpecification searchSpecification = searchSpecification(formatInput(userInput), searchLocation);

        searchApi.search(searchSpecification, new FuzzyOutcomeCallback() {
            @Override
            public void onSuccess(@NotNull FuzzyOutcome fuzzyOutcome) {
                List<Place> places = new ArrayList<>();

                for (FuzzySearchDetails details : fuzzyOutcome.getFuzzyDetailsList())
                    addNewPlace(places, details);

                callback.onSuccess(places);
            }

            @Override
            public void onError(@NotNull SearchException e) {
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void close() {
        searchApi.cancelSearchIfRunning();
    }

    private FuzzySearchEngineDescriptor searchEngineDescriptor(int maxResults) {
        return new FuzzySearchEngineDescriptor.Builder()
                .minFuzzyLevel(1)
                .limit(maxResults)
                .maxFuzzyLevel(3)
                .typeAhead(true)
                .build();
    }

    private String formatInput(String userInput) {
        return userInput.replaceFirst(" ", "").toLowerCase();
    }

    private FuzzySearchSpecification searchSpecification(String userInput, LatLng searchLocation) {
        return new FuzzySearchSpecification.Builder(userInput)
                .searchEngineDescriptor(searchEngineDescriptor)
                .locationDescriptor(locationDescriptor(searchLocation))
                .build();
    }

    private FuzzyLocationDescriptor locationDescriptor(LatLng searchLocation) {
        return new FuzzyLocationDescriptor.Builder()
                .positionBias(new LatLngBias(searchLocation, searchRadius))
                .build();
    }

    private void addNewPlace(List<Place> places, FuzzySearchDetails newPlaceDetails) {
        if(shouldAddPlace(places, newPlaceDetails))
            places.add(new Place(newPlaceDetails));
    }

    private boolean shouldAddPlace(List<Place> places, FuzzySearchDetails newDetails) {
        if(!isRedundant(newDetails))
            for (Place place : places)
                if (place.getAddress().contains(new Place(newDetails).getAddress()))
                    return false;
        return true;
    }

    private boolean isRedundant(FuzzySearchDetails details) {
        return details.getPoi().getName().isEmpty() && details.getAddress().getMunicipalitySubdivision().isEmpty();
    }
}
