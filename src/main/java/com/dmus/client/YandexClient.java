package com.dmus.client;

import com.dmus.dto.GptRequest;
import com.dmus.dto.GptResponse;
import com.dmus.dto.Transcription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class YandexClient {
    @Value("${yandex.api.key}")
    private String apiKey;

    @Value("${yandex.api.chat.url}")
    private String chatApiUrl;

    @Value("${yandex.api.chat.model}")
    private String chatModel;

    @Value("${yandex.api.chat.system_role}")
    private String systemRole;

    @Value("${yandex.api.transcription.url}")
    private String transcriptionApiUrl;

/*    @Value("${yandex.api.transcription.model}")
    private String voiceModel;*/

    @Value("${yandex.api.transcription.language}")
    private String language;

    private final RestTemplate restTemplate; //TODO: redesign to RestClient
    private final ObjectMapper objectMapper;

    public String promptModel(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        GptRequest body = GptRequest.builder()
                .model(chatModel)
                .messages(List.of(
                        GptRequest.Message.builder()
                                .role("system")
                                .content(systemRole)
                                .build(),
                        GptRequest.Message.builder()
                                .role("user")
                                .content(prompt)
                                .build()
                ))
                .build();

        HttpEntity<GptRequest> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(chatApiUrl, request, String.class);
        log.info("Response from gpt: {}", response);
        GptResponse responseBody;
        try {
            responseBody = objectMapper.readValue(response.getBody(), GptResponse.class);
            log.info("Deserialized response from gpt: {}", responseBody);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("There's an error when parsing JSON response from GPT", e);
        }
        return responseBody.getResult().getAlternatives().getFirst().getMessage().getContent();
    }

    public String transcribe(File audio) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        //headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        FileSystemResource fileResource = new FileSystemResource(audio);
        Transcription transcription;
        try {
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileResource.getContentAsByteArray(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(transcriptionApiUrl, requestEntity, String.class);
            transcription = objectMapper.readValue(response.getBody(), Transcription.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("There was an error when converting JSON response to DTO", e);
        } catch (IOException e) {
            throw new IllegalStateException("There was an error when reading file", e);
        }
        log.info("transcribe > return '{}'", transcription.result());
        return transcription.result();
    }
}
