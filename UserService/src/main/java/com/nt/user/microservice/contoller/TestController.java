package com.nt.user.microservice.contoller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
  @GetMapping("/check")
  public String check() {
    return "testing here";
  }
}





