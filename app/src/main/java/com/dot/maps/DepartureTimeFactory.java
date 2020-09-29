package com.dot.maps;

import android.content.Context;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.RoutingException;
import com.tomtom.online.sdk.routing.route.RouteCalculationDescriptor;
import com.tomtom.online.sdk.routing.route.RouteCallback;
import com.tomtom.online.sdk.routing.route.RouteDescriptor;
import com.tomtom.online.sdk.routing.route.RoutePlan;
import com.tomtom.online.sdk.routing.route.RouteSpecification;
import com.tomtom.online.sdk.routing.route.calculation.InstructionsType;
import com.tomtom.online.sdk.routing.route.calculation.TrafficInformation;
import com.tomtom.online.sdk.routing.route.description.AvoidType;
import com.tomtom.online.sdk.routing.route.description.Summary;
import com.tomtom.online.sdk.routing.route.description.TravelMode;
import com.tomtom.online.sdk.routing.route.diagnostic.ReportType;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.List;

public class DepartureTimeFactory {
    private final List<AvoidType> avoidTypes;
    private final List<String> bannedVignettes;
    private final RoutingApi api;

    public DepartureTimeFactory(Context context, String apiKey, List<AvoidType> avoidTypes, List<String> bannedVignettes) {
        this.avoidTypes = avoidTypes;
        this.bannedVignettes = bannedVignettes;

        api = OnlineRoutingApi.create(context, apiKey);
    }

    public void request(LatLng origin, LatLng destination, TravelMode travelMode, DateTime arrivalDate, Callback<DateTime> callback) {
        RouteDescriptor routeDescriptor = routeDescriptor(travelMode);
        RouteCalculationDescriptor routeCalculationDescriptor = routeCalculationDescriptor(routeDescriptor, arrivalDate);
        RouteSpecification routeSpecification = routeSpecification(origin, destination, routeCalculationDescriptor);

        api.planRoute(routeSpecification, new RouteCallback() {
            @Override
            public void onSuccess(@NotNull RoutePlan routePlan) {
                Summary summary = routePlan.getRoutes().get(0).getSummary();
                callback.onSuccess(arrivalDate.plusSeconds(-summary.getNoTrafficTravelTimeInSeconds() - summary.getLiveTrafficIncidentsTravelTimeInSeconds()));
            }

            @Override
            public void onError(@NotNull RoutingException e) {
                callback.onFailure(e);
            }
        });
    }

    private RouteDescriptor routeDescriptor(TravelMode travelMode) {
        return new RouteDescriptor.Builder()
                .considerTraffic(true)
                .travelMode(travelMode)
                .avoidType(avoidTypes)
                .build();
    }

    private RouteCalculationDescriptor routeCalculationDescriptor(RouteDescriptor descriptor, DateTime arrivalDate) {
        return new RouteCalculationDescriptor.Builder()
                .routeDescription(descriptor)
                .reportType(ReportType.NONE)
                .instructionType(InstructionsType.NONE)
                .maxAlternatives(0)
                .computeTravelForTraffic(TrafficInformation.ALL)
                .avoidVignettes(bannedVignettes)
                .arriveAt(arrivalDate.toDate())
                .build();
    }

    private RouteSpecification routeSpecification(LatLng origin, LatLng destination, RouteCalculationDescriptor calculationDescriptor) {
        return new RouteSpecification.Builder(origin, destination)
                .routeCalculationDescriptor(calculationDescriptor)
                .build();
    }
}
