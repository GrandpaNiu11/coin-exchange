package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authorization-server")
public interface OAuth2FeignClient {



    @PostMapping("/oauth/token")
    public ResponseEntity<JwtToken> getUserInfo(
         @RequestParam("grant_type") String grant_type,
         @RequestParam("username")  String username,
         @RequestParam("password")   String password,
         @RequestParam("login_type")   String login_type,
         @RequestHeader("Authorization") String authorization
    );


}
