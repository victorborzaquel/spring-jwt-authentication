package com.victor.springjwtauthentication.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoResource {

        @RequestMapping
        public ResponseEntity<String> hello() {
            return ResponseEntity.ok("Hello World!");
        }
}
