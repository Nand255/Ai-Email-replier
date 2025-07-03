package com.email.writer.service;

import com.email.writer.dto.EmailRequest;

public interface IEmailGeneratorService {
    String generateEmailReply(EmailRequest emailRequest);
}
