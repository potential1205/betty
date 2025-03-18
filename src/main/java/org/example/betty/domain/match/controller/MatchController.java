package org.example.betty.domain.match.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchController {

    @GetMapping("/test")
    public String testServer() {
        return "EC2 서버가 정상적으로 동작합니다!";
    }
}
