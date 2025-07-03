package com.email.writer.controller;

import com.email.writer.dto.EmailRequest;
import com.email.writer.service.IEmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmailGeneratorController {

    private final IEmailGeneratorService service;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        String response = service.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }

}
