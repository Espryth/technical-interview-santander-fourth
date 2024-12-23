package me.jarvica.santander.fourthexercise.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRoleController {

  @GetMapping("/user")
  String user() {
    return "User";
  }

  @GetMapping("/admin")
  String admin() {
    return "Admin";
  }
}
