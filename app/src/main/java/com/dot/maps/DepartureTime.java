package com.dot.maps;

import com.tomtom.online.sdk.routing.route.description.Summary;
import com.tomtom.online.sdk.routing.route.information.FullRoute;

import java.util.Objects;

public class DepartureTime {
    private final String hour;
    private final String minute;

    public DepartureTime(FullRoute route) {
        this(route.getSummary());
    }

    public DepartureTime(Summary summary) {
        this(Objects.requireNonNull(summary.getDepartureTime()));
    }

    public DepartureTime(String departure) {
        String[] watchSegment = departure.split("T")[1].split("/+")[0].split(":");

        hour = watchSegment[0];
        minute = watchSegment[1];
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }
}
