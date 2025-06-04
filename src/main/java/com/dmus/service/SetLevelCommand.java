package com.dmus.service;

import com.dmus.client.YandexClient;
import com.dmus.telegram.Bot;
import com.dmus.utils.InterviewLevel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

@Component
public class SetLevelCommand extends Command {

    private static final String COMMAND_NAME = "/setlevel";
    private static final String LEVEL_SET_MSG = "Interview level was set to: ";
    private static final String LEVEL_SET_INVALID_PARAM_MSG = "Invalid interview level, must be set from interval: ";
    private static final String LEVEL_SET_ERROR_MSG = "The was an error parsing the level number. Pls provide numeric parameter.";

    public SetLevelCommand(DBServiceQuestion questionService, YandexClient yandexClient, InterviewSessionsService interviewSessionsService, Producer producer) {
        super(questionService, yandexClient, interviewSessionsService, producer);
    }

    @Override
    public boolean isApplicable(Update update) {
        Message message = update.getMessage();
        return message.hasText() && message.getText().startsWith(COMMAND_NAME);
    }

    // setlevel command executed at the middle of interview will clear current session data(rewrite questions/answers)
    // if exists for current user, so better should be used at the beginning of the interview
    @Override
    public String process(Update update, Bot bot) {
        String command = update.getMessage().getText();
        String levelStr = command.substring(9).trim();
        try {
            long levelInt = Long.parseLong(levelStr);
            InterviewLevel level = InterviewLevel.getLevelByLong(levelInt);
            if (level == null) {
                return LEVEL_SET_INVALID_PARAM_MSG + Arrays.toString(InterviewLevel.values());
            }
            String userName = update.getMessage().getFrom().getUserName();
            interviewSessionsService.setLevel(userName, level);
            return LEVEL_SET_MSG + level.name();
        } catch (NumberFormatException e) {
            return LEVEL_SET_ERROR_MSG;
        }
    }

}
