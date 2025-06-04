package com.dmus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GptResponse {

    private Result result;

    @Data
    public static class Result {
        @JsonProperty("alternatives")
        private List<Alternative> alternatives;
    }

    @Data
    public static class Alternative {
        private Message message;
        private String status;
    }

    @Data
    public static class Message {
        private String role;
        @JsonProperty("text")
        private String content;
    }
}
