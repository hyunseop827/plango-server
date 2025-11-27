package com.plango.server.travel.util;

/**
 * Utility class for comparing location names from AI and Google Geocoding API
 */
public class StringValidationUtil {

    /**
     * Check if two location names are similar enough to be considered a match
     * 
     * @param aiPlaceName Location name provided by AI
     * @param apiPlaceName Location name returned from Google Geocoding API
     * @return true if the names are similar, false otherwise
     */
    public static boolean isSimilar(String aiPlaceName, String apiPlaceName) {
        if (aiPlaceName == null || apiPlaceName == null) {
            return false;
        }

        // Normalize strings: lowercase and trim
        String ai = aiPlaceName.toLowerCase().trim();
        String api = apiPlaceName.toLowerCase().trim();

        // Empty check
        if (ai.isEmpty() || api.isEmpty()) {
            return false;
        }

        // Check if one contains the other
        // Example: "협재해변" is contained in "협재해변, 제주특별자치도 제주시 한림읍"
        return api.contains(ai) || ai.contains(api);
    }

    /**
     * Calculate similarity score between two strings (0.0 to 1.0)
     * Uses simple character matching ratio
     * 
     * @param str1 First string
     * @param str2 Second string
     * @return Similarity score (0.0 = completely different, 1.0 = identical)
     */
    public static double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }

        String s1 = str1.toLowerCase().trim();
        String s2 = str2.toLowerCase().trim();

        if (s1.isEmpty() || s2.isEmpty()) {
            return 0.0;
        }

        if (s1.equals(s2)) {
            return 1.0;
        }

        // Count matching characters
        int matches = 0;
        int minLength = Math.min(s1.length(), s2.length());

        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                matches++;
            }
        }

        return (double) matches / Math.max(s1.length(), s2.length());
    }

    /**
     * Check if similarity score meets the threshold
     * 
     * @param str1 First string
     * @param str2 Second string
     * @param threshold Minimum similarity score (0.0 to 1.0)
     * @return true if similarity >= threshold
     */
    public static boolean isSimilarWithThreshold(String str1, String str2, double threshold) {
        return calculateSimilarity(str1, str2) >= threshold;
    }
}
