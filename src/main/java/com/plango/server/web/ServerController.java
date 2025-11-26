package com.plango.server.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for checking server's health
 */
@RestController
public class ServerController {

    @GetMapping("/health")
    public String health(){
        return "Server is running";
    }
}
