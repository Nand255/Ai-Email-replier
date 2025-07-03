package com.email.writer.service;

import com.email.writer.dto.EmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorServiceImpl implements IEmailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String generateEmailReply(EmailRequest emailRequest) {

        // Build the prompt
        String prompt = buildPrompt(emailRequest);

        // Craft a request
        Map<String,Object> requestBody = Map.of("contents", new Object[] {
                                            Map.of("parts",new Object[] {
                                                    Map.of("text", prompt)
                                            })
                                        }
        );

        // Do request and get a response
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //Extract Response and Return Response

        return ExtractResponseContent(response);
    }

    private String ExtractResponseContent(String response){
        try {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        return rootNode.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();
        } catch (JsonProcessingException e) {
            return "Error processing request : "+e.getMessage();
        } catch (Exception e) {
            return "something went wrong : "+e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest){

        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for hte following email content. Please don't generate a subject line ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());

        return prompt.toString();
    }

}
