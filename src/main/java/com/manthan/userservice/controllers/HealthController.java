package com.manthan.userservice.controllers;

import com.manthan.userservice.dtos.HealthCheckResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HealthController {
    @GetMapping("/")
    public ResponseEntity<HealthCheckResponseDTO> healthCheck(){
        HealthCheckResponseDTO responseDTO = new HealthCheckResponseDTO();
        responseDTO.setVersion("v5.0");
        responseDTO.setTimestamp(new Date());

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
