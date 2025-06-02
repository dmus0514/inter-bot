package com.dmus.service;

import com.dmus.client.YandexClient;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.dmus.telegram.Bot;

@RequiredArgsConstructor
public abstract class Command {

    protected final DBServiceQuestion questionService;
    protected final YandexClient yandexClient;
    protected final InterviewSessionsService interviewSessionsService;
    protected final Producer producer;

    public abstract boolean isApplicable(Update update);

    public abstract String process(Update update, Bot bot);
}
