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
    private final int maxResults;
    private final double searchRadius;

    public PlaceFactory(Context context, String apiKey, int maxResults, double searchRadius) {
        this.maxResults = maxResults;
        this.searchRadius = searchRadius;
        searchApi = OnlineSearchApi.create(context, apiKey);
    }

    public void request(LatLng searchLocation, String userInput, Callback<List<Place>> callback) {
        userInput = userInput.replaceFirst(" ", "").toLowerCase();

        FuzzySearchEngineDescriptor fuzzySearchEngineDescriptor = new FuzzySearchEngineDescriptor.Builder()
                .minFuzzyLevel(1)
                .limit(maxResults)
                .maxFuzzyLevel(3)
                .typeAhead(true)
                .build();
        FuzzyLocationDescriptor fuzzyLocationDescriptor = new FuzzyLocationDescriptor.Builder()
                .positionBias(new LatLngBias(searchLocation, searchRadius))
                .build();
        FuzzySearchSpecification searchSpecification = new FuzzySearchSpecification.Builder(userInput)
                .searchEngineDescriptor(fuzzySearchEngineDescriptor)
                .locationDescriptor(fuzzyLocationDescriptor)
                .build();

        searchApi.search(searchSpecification, new FuzzyOutcomeCallback() {
            @Override
            public void onSuccess(@NotNull FuzzyOutcome fuzzyOutcome) {
                List<Place> places = new ArrayList<>();
                boolean shouldAdd;

                for (FuzzySearchDetails details : fuzzyOutcome.getFuzzyDetailsList()) {
                    shouldAdd = true;

                    if(!isRedundant(details))
                        for (Place place : places)
                            if (place.getAddress().contains(new Place(details).getAddress()))
                                shouldAdd = false;

                    if(shouldAdd)
                        places.add(new Place(details));
                }

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

    private boolean isRedundant(FuzzySearchDetails details) {
        return details.getPoi().getName().isEmpty() && details.getAddress().getMunicipalitySubdivision().isEmpty();
    }
}
