package com.dmus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record GptRequest(@JsonProperty("modelUri") String model, List<Message> messages){
    @Builder
    public record Message(String role, @JsonProperty("text") String content){}
}
