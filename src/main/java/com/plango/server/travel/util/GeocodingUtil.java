package com.plango.server.travel.util;

import com.plango.server.exception.ApiNotWorkingException;
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
     * returning coordinate from location name
     * If the location isn't valid, it just gives null,
     * Client just can ignore null and use infos from AI
     */
    public static Coordinate getLocationCoordinateWithLocationName(String location, String apiKey) {

        try {
            // 1. Encode address. ' ' -> %20
            String encodedAddress = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = String.format(FORWARD_GEOCODING_URL, encodedAddress, apiKey);

            // 2. call API and parse to Map
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                return null;
            }

            String status = (String) response.get("status");
            if (!"OK".equals(status)) {
               return null;
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null || results.isEmpty()) {
                return null;
            }

            // "address_components"
            Map<String, Object> result = results.get(0);

            // "geometry"
            Map<String,Object> geometry = (Map<String,Object>)result.get("geometry");

            // "location"
            Map<String, Object> loc =
                    (Map<String, Object>) geometry.get("location");

            double latitude = (Double)loc.get("lat");
            double longitude = (Double)loc.get("lng");

            return new Coordinate(latitude, longitude);

        } catch (Exception e) {
            throw new ApiNotWorkingException("GeocodingUtil","지오 코딩 오류!",e.getMessage());
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
