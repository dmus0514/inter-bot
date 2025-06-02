package com.dmus.service;

import com.dmus.client.YandexClient;
import com.dmus.model.AskedQuestion;
import com.dmus.model.Interview;
import com.dmus.model.User;
import com.dmus.utils.InterviewLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.dmus.dto.Question;
import com.dmus.telegram.Bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class VoiceCommand extends Command {

    private static final String QUESTION_PROMPT = """
                Вот исходный вопрос для собеседования на Java %s в Java: %s
                Задай его, придумав интересную ситуацию c каким-нибудь реальным очень известным
                приложением, в контексте которого и будет задаваться вопрос. Чтобы это не выглядело сухо, как
                просто вопрос по Java. Подумай, как можно сделать его более интересным и понятным для кандидата
                с помощью дополнительного контекста реального приложения.
                
                Учитывай, что кандидат пока может не знать
                про веб-приложения и сложную разработку. Но он знает про популярные приложения, которые есть
                в мире, такие как YouTube, Tinder, Twitter, Amazon или Telegram. Но не используй Meta, Facebook или
                Instagram в качестве своих примеров. Прочие же приложения могут участвовать в формировании
                контекста для твоих вопросов, чтобы сделать собеседование более интересным для кандидата.
        
                Пример: Представим, что мы работаем в Google над приложением YouTube. И нам поручают задачу:
                создать класс видео, в котором будет 3 поля: название, количество лайков и количество просмотров.
                Добавляя эти поля, нам важно соблюсти принцип инкапсуляции в ООП. Что это такое, и как он
                реализуется в Java?
                
                Используй и другие примеры известных приложений, не только YouTube. Но иногда можно и YouTube.
                Кроме того, пример выше - про инкапсуляцию. Но ты задавай вопрос конкретно на ту тему, что
                указана в самом начале промпта. Пример используй только для понимания, как мог бы выглядеть
                вопрос по стилю и контексту, но его смысловое наполнение должно относиться именно к выбранной
                выше теме.
                
                Стиль общения:
                Общайся с кандидатом на "ты". Это должна быть беседа двух хороших друзей, тепло и непринужденно,
                без лишних формальностей.
                Избегай слишком многословных формулировок. Старайся формулировать предложения четко и по делу,
                но в то же время сохраняй ламповость и живость беседы. Нужно найти баланс. Тем не менее,
                человек не должен получить от тебя огромный текст, который ему будет лень читать. Разговор
                должен быть интересным, но лаконичным, чтобы человек не потерял желание его продолжать из-за
                огромного количества текста на экране.
                """;
    private static final String FEEDBACK_PROMPT = """
                Проанализируй вопросы на собеседовании для позиции %s Java Developer и те ответы, которые на каждый
                из них дал кандидат и предоставь обратную связь для кандидата по тому, насколько хорошо ему удалось ответить
                на эти вопросы именно для этого уровня собеседования.
                Каждому вопросу соответствует ответ, который следует сразу за ним. Вопросы могут
                быть не связаны друг с другом, поэтому делай оценку относительно ответов, данных на конкретные вопросы,
                а не всех вместе.
            
                Давая обратную связь, сначала расскажи о том, что у кандидата получилось хорошо, какие темы он знает
                действительно глубоко и где конкретно ему удалось показать очень хороший уровень.
            
                После обрати внимание на ответы, которые были неверны или верны лишь наполовину. Конкретно укажи, где
                кандидат ошибся в своих утверждениях на тот или иной вопрос и опиши, как он мог бы улучшить свой ответ.
                Давай достаточно подробную обратную связь в этом месте, чтобы кандидат четко понимал свои ошибки и мог
                сделать из них выводы и чему-то научиться буквально из этой обратной связи.
            
                Затем подскажи кандидату, на какие темы он мог бы обратить больше внимания при дальнейшей подготовке,
                чтобы заполнить те пробелы, которые были обнаружены у него в процессе того собеседования, что ты
                анализируешь.
            
                Стиль общения:
                Общайся с кандидатом на "ты". Это должна быть беседа двух хороших друзей, тепло и непринужденно,
                без лишних формальностей.
                Избегай слишком многословных формулировок. Старайся формулировать предложения четко и по делу,
                но в то же время сохраняй ламповость и живость беседы. Нужно найти баланс. Тем не менее,
                человек не должен получить от тебя огромный текст, который ему будет лень читать. Разговор
                должен быть интересным, но лаконичным, чтобы человек не потерял желание его продолжать из-за
                огромного количества текста на экране.
            
                Форматирование:
                Разделяй свою обратную связь на абзацы после каждых 4-5 предложений. Отдели блоки "Что получилось хорошо",
                "Что можно улучшить" и "На что обратить внимание" соответствующими заголовками отдельными от основного
                содержимого этих блоков.
            
                Результаты собеседования:
            
            """;

    @Value("${interview.max-questions}")
    private int maxQuestions;

    @Autowired
    DBServiceUser dbServiceUser;

    public VoiceCommand(YandexClient yandexClient,
                        InterviewSessionsService interviewSessionsService,
                        DBServiceQuestion questionService,
                        Producer producer,
                        DBServiceUser dbServiceUser) {
        super(questionService, yandexClient, interviewSessionsService, producer);
        this.dbServiceUser = dbServiceUser;
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.getMessage().hasVoice();
    }

    @Override
    public String process(Update update, Bot bot) {
        log.trace("process > start processing voice message");
        String answer = transcribeVoiceAnswer(update, bot);
        log.debug("process > received transcribed answer from gpt: \n '{}'", answer);

        String userName = update.getMessage().getFrom().getUserName();
        try {
            var question = interviewSessionsService.addAnswer(userName, answer);
            interviewSessionsService.stopTimer(userName);

            producer.sendMessage(question);

            InterviewLevel level = interviewSessionsService.getLevel(userName);
            if (interviewSessionsService.getAskedQuestionsNumberPerUser(userName) == maxQuestions) {
                return provideFeedback(update, level);
            } else {
                return askNextQuestion(update, level, bot);
            }
        } catch (Exception e) {
            log.error("process > Exception in processing voice message", e);
            return "Что-то пошло не так! Возможно нет активного собеседования! Пожалуйста начните заново!";
        }
    }

    private String transcribeVoiceAnswer(Update update, Bot bot) {
        Voice voice = update.getMessage().getVoice();
        String fileId = voice.getFileId();
        java.io.File audio;
        try {
            GetFile getFileRequest = new GetFile();
            getFileRequest.setFileId(fileId);
            File file = bot.execute(getFileRequest);

            audio = bot.downloadFile(file.getFilePath());
        } catch (TelegramApiException e) {
            throw new IllegalStateException("There's an error when processing Telegram audio", e);
        }
        return yandexClient.transcribe(renameToOgg(audio));
    }

    private java.io.File renameToOgg(java.io.File tmpFile) {
        String fileName = tmpFile.getName();
        String newFileName = fileName.substring(0, fileName.length() - 4) + ".ogg";
        Path sourcePath = tmpFile.toPath();
        Path targetPath = sourcePath.resolveSibling(newFileName);
        try {
            Files.move(sourcePath, targetPath);
        } catch (IOException e) {
            throw new IllegalStateException("There was an error when renaming .tmp audio file to .ogg", e);
        }
        return targetPath.toFile();
    }

    private String askNextQuestion(Update update, InterviewLevel level, Bot bot) {
        log.trace("askNextQuestion > start processing");

        String baseQuestion = questionService.getRandomQuestion(level.getLevelNum());
        if (baseQuestion == null) {
            return "Для выбранного уровня нет вопросов!";
        }
        log.trace("askNextQuestion > configured level: '{}', selected baseQuestion: '{}'", level, baseQuestion);

        String prompt = String.format(QUESTION_PROMPT, level.name(), baseQuestion);
        String question = yandexClient.promptModel(prompt);
        log.debug("askNextQuestion > received question from chatgpt: \n '{}'", question);

        interviewSessionsService.addQuestionAndStartTimer(update.getMessage().getFrom().getUserName(), question,
                update.getMessage().getChatId(), bot);

        return question;
    }

    private String provideFeedback(Update update, InterviewLevel level) {
        log.trace("provideFeedback > start processing");

        StringBuilder feedbackPrompt = new StringBuilder();
        feedbackPrompt.append(String.format(FEEDBACK_PROMPT, level.name()));

        org.telegram.telegrambots.meta.api.objects.User tgUser = update.getMessage().getFrom();
        String userName = tgUser.getUserName();

        Deque<Question> questions = interviewSessionsService.finishInterview(userName);
        questions.forEach(question -> feedbackPrompt.append("Исходный вопрос: ")
                .append(question.getQuestion()).append("\n")
                .append("Ответ кандидата: ")
                .append(question.getAnswer()).append("\n"));

        Set<AskedQuestion> askedQuestions = new HashSet<>();
        questions.forEach(question -> askedQuestions.add(
                new AskedQuestion(null, null, (long)(Math.random() * 10 + 1), question.getQuestion(), question.getAnswer())
        ));
        log.debug("provideFeedback > askedQuestions: {}", askedQuestions);
        String feedback = yandexClient.promptModel(feedbackPrompt.toString());
        Integer resultGrade = 5; //TODO: add request to prompt about user grade
        /*long epochFromDate = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond();*/

        Interview interview = new Interview(null, null, LocalDateTime.now(), 1L, feedback, resultGrade, askedQuestions);

        var userOptional = dbServiceUser.getUserByName(userName);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            user.interviews().add(interview);
            dbServiceUser.saveUser(user);
            //dbServiceUser.saveUser(new User(user.id(), user.getFirstName(), user.getLastName(), user.getUserName(), null, interviews));
        } else {
            dbServiceUser.saveUser(new User(null, tgUser.getFirstName(), tgUser.getLastName(), tgUser.getUserName(),
                    null, Set.of(interview)));
        }

        return yandexClient.promptModel(feedbackPrompt.toString());
    }
}
