package com.dot.maps;

import android.content.Context;
import com.google.maps.PendingResult;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.RoutingException;
import com.tomtom.online.sdk.routing.route.*;
import com.tomtom.online.sdk.routing.route.calculation.TrafficInformation;
import com.tomtom.online.sdk.routing.route.description.AvoidType;
import com.tomtom.online.sdk.routing.route.description.RouteType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class DepartureTimeFactory {
    private final List<AvoidType> avoidTypes;
    private final RoutingApi api;

    public DepartureTimeFactory(Context context, String apiKey, List<AvoidType> avoidTypes) {
        this.avoidTypes = avoidTypes;
        api = OnlineRoutingApi.create(context, apiKey);
    }

    public void request(LatLng origin, LatLng destination, com.tomtom.online.sdk.routing.route.description.TravelMode travelMode, Date arrivalDate, PendingResult.Callback<String> callback) {
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .considerTraffic(true)
                .travelMode(travelMode)
                .routeType(RouteType.FASTEST)
                .avoidType(avoidTypes)
                .build();

        RouteCalculationDescriptor routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                .routeDescription(routeDescriptor)
                .computeTravelForTraffic(TrafficInformation.ALL)
                .arriveAt(arrivalDate)
                .build();

        RouteSpecification routeSpecification = new RouteSpecification.Builder(origin, destination)
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();

        api.planRoute(routeSpecification, new RouteCallback() {
            @Override
            public void onSuccess(@NotNull RoutePlan routePlan) {
                callback.onResult(routePlan.getRoutes().get(0).getSummary().getDepartureTime());
            }

            @Override
            public void onError(@NotNull RoutingException e) {
                callback.onFailure(e);
            }
        });
    }
}
