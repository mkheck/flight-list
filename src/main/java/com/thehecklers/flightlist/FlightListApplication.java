package com.thehecklers.flightlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class FlightListApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightListApplication.class, args);
    }

}

@Controller
class ListController {
    private final WebClient client = WebClient.create("http://localhost:9090");

    @GetMapping("/positions")
    String getPositions(Model model,
                     @RequestParam(required = false) String oc,
                     @RequestParam(required = false) String tracklo,
                     @RequestParam(required = false) String trackhi) {
        var ocParam = (null == oc ? "" : "oc=" + oc);
        var trackParams = ((null == tracklo) || (null == trackhi) ? "" : "tracklo=" + tracklo +
                "&trackhi=" + trackhi);
        var allParams = ocParam +
                (trackParams.length() > 0 ? "&" + trackParams : "");

        model.addAttribute("positions", client.get()
                .uri("/positions" + (allParams.length() > 0 ? "?" + allParams : ""))
                .retrieve()
                .bodyToFlux(Position.class));

        return "displaypositions";
    }

    @GetMapping("/countries")
    String getCountries(Model model) {
        model.addAttribute("countries", client.get()
                .uri("/countries")
                .retrieve()
                .bodyToFlux(String.class));

        return "displaycountries";
    }
}

record Position(String icao24,
                String callsign,
                String origin_country,
                float longitude,
                float latitude,
                float baro_altitude,
                float velocity,
                float true_track,
                float vertical_rate,
                float geo_altitude,
                String squawk,
                boolean spi,
                int position_source) {}
