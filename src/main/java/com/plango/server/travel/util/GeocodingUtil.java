package com.plango.server.travel.util;

import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for validating latitude/longitude and
 * converting coordinates to a human-readable place name using Google Geocoding API.
 */
public final class GeocodingUtil {

    private static final String REVERSE_GEOCODING_URL =
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=%s&language=ko";
    
    private static final String FORWARD_GEOCODING_URL =
            "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s&language=ko";

    private GeocodingUtil() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Simple record for coordinates
     */
    public record Coordinates(double lat, double lng) {}

    /**
     * Check if lat/lng is within valid range
     *
     * @param lat latitude
     * @param lng longitude
     * @return true if valid, false otherwise
     */
    public static boolean isValidLatLng(double lat, double lng) {
        return lat >= -90 && lat <= 90 &&
                lng >= -180 && lng <= 180;
    }

    /**
     * Reverse geocoding: coordinates to place name
     *
     * @param lat   latitude
     * @param lng   longitude
     * @param apiKey Google Maps Geocoding API Key
     * @return formatted address string
     */
    public static Optional<String> getPlaceNameFromCoordinates(
            double lat, double lng, String apiKey) {

        try {
            String url = String.format(REVERSE_GEOCODING_URL, lat, lng, apiKey);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                return Optional.empty();
            }

            String status = (String) response.get("status");
            if (!"OK".equals(status)) {
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) response.get("results");

            if (results == null || results.isEmpty()) {
                return Optional.empty();
            }

            Map<String, Object> first = results.get(0);
            String formattedAddress = (String) first.get("formatted_address");

            if (formattedAddress == null || formattedAddress.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(formattedAddress);

        } catch (Exception e) {
            System.err.println("Geocoding API call failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Validate if AI place name matches coordinates via reverse geocoding
     *
     * @param aiPlaceName place name from AI
     * @param lat         latitude
     * @param lng         longitude
     * @param apiKey      Google Maps Geocoding API Key
     * @return true if valid, false otherwise
     */
    public static boolean validateCoordinate(
            String aiPlaceName, double lat, double lng, String apiKey) {

        if (aiPlaceName == null || aiPlaceName.isBlank()) {
            return false;
        }

        if (!isValidLatLng(lat, lng)) {
            return false;
        }

        Optional<String> apiPlaceNameOpt = getPlaceNameFromCoordinates(lat, lng, apiKey);

        if (apiPlaceNameOpt.isEmpty()) {
            return false;
        }

        String apiPlaceName = apiPlaceNameOpt.get();

        return StringValidationUtil.isSimilar(aiPlaceName, apiPlaceName);
    }

    /**
     * Forward geocoding: place name to coordinates
     *
     * @param placeName place name
     * @param apiKey    Google Maps Geocoding API Key
     * @return coordinates (lat, lng)
     */
    public static Optional<Coordinates> getCoordinatesFromPlaceName(
            String placeName, String apiKey) {

        if (placeName == null || placeName.isBlank()) {
            return Optional.empty();
        }

        try {
            String encodedPlace = java.net.URLEncoder.encode(placeName, "UTF-8");
            String url = String.format(FORWARD_GEOCODING_URL, encodedPlace, apiKey);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                return Optional.empty();
            }

            String status = (String) response.get("status");
            if (!"OK".equals(status)) {
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) response.get("results");

            if (results == null || results.isEmpty()) {
                return Optional.empty();
            }

            Map<String, Object> first = results.get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> geometry = (Map<String, Object>) first.get("geometry");
            
            if (geometry == null) {
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> location = (Map<String, Object>) geometry.get("location");
            
            if (location == null) {
                return Optional.empty();
            }

            Object latObj = location.get("lat");
            Object lngObj = location.get("lng");

            if (latObj == null || lngObj == null) {
                return Optional.empty();
            }

            double lat = ((Number) latObj).doubleValue();
            double lng = ((Number) lngObj).doubleValue();

            return Optional.of(new Coordinates(lat, lng));

        } catch (Exception e) {
            System.err.println("Forward Geocoding API call failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Validate and correct AI coordinates
     * 
     * @param aiPlaceName place name from AI
     * @param aiLat       latitude from AI
     * @param aiLng       longitude from AI
     * @param apiKey      Google Maps API Key
     * @return corrected coordinates if needed, empty otherwise
     */
    public static Optional<Coordinates> validateAndCorrectCoordinates(
            String aiPlaceName, double aiLat, double aiLng, String apiKey) {

        boolean isValid = validateCoordinate(aiPlaceName, aiLat, aiLng, apiKey);

        if (isValid) {
            return Optional.empty();
        }

        return getCoordinatesFromPlaceName(aiPlaceName, apiKey);
    }

    private static boolean isClose(double lat1, double lng1, double lat2, double lng2) {
        double latDiff = Math.abs(lat1 - lat2);
        double lngDiff = Math.abs(lng1 - lng2);

        // roughly 0.0005 degrees = ~50m
        return latDiff < 0.0005 && lngDiff < 0.0005;
    }
}
