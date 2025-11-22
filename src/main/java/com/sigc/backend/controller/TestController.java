package com.sigc.backend.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class TestController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/hash")
    public Map<String, Object> generateHash(@RequestParam String password) {
        String hash = encoder.encode(password);
        Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        response.put("hash", hash);
        response.put("matches", encoder.matches(password, hash));
        return response;
    }

    @GetMapping("/verify")
    public Map<String, Object> verifyHash(@RequestParam String password, @RequestParam String hash) {
        Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        response.put("hash", hash);
        response.put("matches", encoder.matches(password, hash));
        return response;
    }
}
