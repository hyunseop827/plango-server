package com.plango.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

    @GetMapping("/health")
    public String health(){
        return "Server is running";
    }
}
