package com.dmus.service;

import com.dmus.client.YandexClient;
import com.dmus.utils.InterviewLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.dmus.repository.InterviewSessionsRepository;
import com.dmus.telegram.Bot;

import static com.dmus.utils.InterviewLevel.Junior;

@Slf4j
@Component
public class StartCommand extends Command {

    private static final String COMMAND_NAME = "/start";
    private static final String INTERVIEW_PROMPT = """
                        Весело и тепло поприветствуй кандидата на собеседовании, а также расскажи ему о правилах интервью:
                        1. Будет 10 вопросов по Java на уровень Java %s
                        2. Вопросы будут по разным темам языка Java
                        3. Отвечать на вопросы кандидат может голосовыми сообщениями и так подробно, как считает нужным
                        4. По итогам его 10 ответов будет предоставлена обратная связь от собеседующего с обозначением
                        сильных сторон, а также мест, где ответы были не очень точны с указанием, на какие аспекты подготовки
                        кандидату нужно обратить внимание
                        
                        Сам следуй правилам выше при формировании вопросов. Учитывай, что кандидат пока может не знать
                        про веб-приложения и сложную разработку. Но он знает про популярные приложения, которые есть
                        в мире, такие как YouTube, Tinder, Twitter или Telegram. Но не используй Meta, Facebook или
                        Instagram в качестве своих примеров. Прочие же приложения могут участвовать в формировании
                        контекста для твоих вопросов, чтобы сделать собеседование более интересным для кандидата.
                 
                        Вот исходный вопрос для собеседования на Java %s в Java: %s
                        Задай его, придумав интересную ситуацию c каким-нибудь реальным очень известным
                        приложением, в контексте которого и будет задаваться вопрос. Чтобы это не выглядело сухо, как
                        просто вопрос по Java. Подумай, как можно сделать его более интересным и понятным для кандидата
                        с помощью дополнительного контекста реального приложения.
                        
                        Пример: Представим, что мы работаем в Google над приложением YouTube. И нам поручают задачу:
                        создать класс видео, в котором будет 3 поля: название, количество лайков и количество просмотров.
                        Добавляя эти поля, нам важно соблюсти принцип инкапсуляции в ООП. Что это такое, и как он
                        реализуется в Java?
                        
                        Используй и другие примеры известных приложений, не только YouTube. Но иногда можно и YouTube.
                        
                        Стиль общения:
                        Общайся с кандидатом на "ты". Это должна быть беседа двух хороших друзей, тепло и непринужденно,
                        без лишних формальностей.
                        Избегай слишком многословных формулировок. Старайся формулировать предложения четко и по делу,
                        но в то же время сохраняй ламповость и живость беседы. Нужно найти баланс. Тем не менее,
                        человек не должен получить от тебя огромный текст, который ему будет лень читать. Разговор
                        должен быть интересным, но лаконичным, чтобы человек не потерял желание его продолжать из-за
                        огромного количества текста на экране.
                        """;

    public StartCommand(DBServiceQuestion questionService,
                        YandexClient yandexClient,
                        InterviewSessionsService interviewSessionsService,
                        Producer producer) {
        super(questionService, yandexClient, interviewSessionsService, producer);
    }

    public boolean isApplicable(Update update) {
        Message message = update.getMessage();
        return message.hasText() && COMMAND_NAME.equals(message.getText());
    }

    public String process(Update update, Bot bot) {
        log.trace("process > start processing {}", update.getMessage().getText());

        String userName = update.getMessage().getFrom().getUserName(); //TODO: currently UserName is used but not all accounts contain it
        InterviewLevel level = interviewSessionsService.getLevel(userName);
        if (level == null) { //if level was not set directly it is defaulted to Junior
            level = Junior;
            interviewSessionsService.setLevel(userName, level);
        }

        String baseQuestion = questionService.getRandomQuestion(level.getLevelNum());
        if (baseQuestion == null) {
            return "Для выбранного уровня нет вопросов!";
        }
        log.trace("process > configured level: '{}', selected baseQuestion: '{}'", level, baseQuestion);

        String prompt = String.format(INTERVIEW_PROMPT, level.name(), level.name(), baseQuestion);
        String question = yandexClient.promptModel(prompt);
        log.debug("process > received question from gpt: \n '{}'", question);

        interviewSessionsService.addQuestionAndStartTimer(userName, question, update.getMessage().getChatId(), bot);

        return question;
    }
}
