package com.dmus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GptRequest {
    @JsonProperty("modelUri")
    private String model;
    private List<Message> messages;

    @Data
    @Builder
    public static class Message {
        private String role;
        @JsonProperty("text")
        private String content;
    }
}
