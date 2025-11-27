package com.plango.server.travel.util;

import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Utility class for giving correct longitude and latitude
 */
public final class GeocodingUtil {

    private static final String FORWARD_GEOCODING_URL =
            "https://maps.googleapis.com/maps/api/geocode/json?" +
                    "address=%s&language=ko&key=%s";

    private GeocodingUtil() { }


    /**
     * Returns coordinate from location name.
     * If the location isn't valid, returns null.
     * Client can ignore null and use coordinates from AI.
     *
     * @param location location name to search
     * @param apiKey Google Maps API key
     * @return Coordinate or null if not found
     */
    public static Coordinate getLocationCoordinateWithLocationName(String location, String apiKey) {
        return getLocationCoordinateWithLocationName(location, null, apiKey);
    }

    /**
     * Returns coordinate from location name with destination context for improved accuracy.
     * If the location isn't valid, returns null.
     * Client can ignore null and use coordinates from AI.
     * 
     * @param location location name to search
     * @param destination destination context (e.g., "Seoul", "Busan") to improve accuracy
     * @param apiKey Google Maps API key
     * @return Coordinate or null if not found
     */
    public static Coordinate getLocationCoordinateWithLocationName(String location, String destination, String apiKey) {
        try {
            // Build search query with destination context for better accuracy
            String searchQuery = (destination != null && !destination.isEmpty()) 
                    ? location + ", " + destination 
                    : location;
            
            String encodedAddress = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String url = String.format(FORWARD_GEOCODING_URL, encodedAddress, apiKey);

            // Call Geocoding API and parse response
            RestTemplate restTemplate = new RestTemplate();
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !"OK".equals(response.get("status"))) {
                return null;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null || results.isEmpty()) {
                return null;
            }

            // Extract coordinates from first result
            Map<String, Object> result = results.get(0);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> locationData = (Map<String, Object>) geometry.get("location");

            double latitude = ((Number) locationData.get("lat")).doubleValue();
            double longitude = ((Number) locationData.get("lng")).doubleValue();

            return new Coordinate(latitude, longitude);

        } catch (Exception e) {
            // Log error and return null to prevent entire travel creation failure
            System.err.println("Geocoding error for location: " + location + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * API json specific answer.
     *
     * response : Map<String, Object>
     *  ├─ "status"  → String
     *  └─ "results" → List<Map<String, Object>>
     *                  └─ [0] → first : Map<String, Object>
     *                        ├─ "address_components" → List<Map<String, Object>>
     *                        ├─ "formatted_address"  → String
     *                        ├─ "geometry"           → Map<String, Object>
     *                        │     └─ "location" → Map<String, Object>
     *                        │           ├─ "lat" → Number
     *                        │           └─ "lng" → Number
     *                        └─ "types"            → List<String>
     */
}
