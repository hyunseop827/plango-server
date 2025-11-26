package com.plango.server.travel.dto;

/**
 * Travel Delete Response
 *
 * @param msg      delete confirmed message
 * @param travelId travel id
 */
public record TravelDeleteResponse(
        String msg,
        String travelId
) {
}
