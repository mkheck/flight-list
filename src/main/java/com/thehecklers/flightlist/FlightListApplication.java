package com.thehecklers.flightlist;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
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
                .bodyToFlux(Position.class)
                .toIterable());

        return "displaypositions";
    }

    @GetMapping("/countries")
    String getCountries(Model model) {
        model.addAttribute("countries", client.get()
                .uri("/countries")
                .retrieve()
                .bodyToFlux(String.class)
                .toIterable());

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

/*
@Data
@NoArgsConstructor
@AllArgsConstructor
class Position {
    private String icao24,
            callsign,
            origin_country;
    private float longitude,
            latitude,
            baro_altitude,
            velocity,
            true_track,
            vertical_rate,
            geo_altitude;
    private String squawk;
    private boolean spi;
    private int position_source;
}
*/
