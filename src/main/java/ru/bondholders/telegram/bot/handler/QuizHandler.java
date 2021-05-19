package ru.bondholders.telegram.bot.handler;

import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.Question;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaQuestionRepository;
import ru.bondholders.telegram.repository.JpaUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.bondholders.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;

@Slf4j
@Component
public class QuizHandler implements Handler {
    // Supported CallBackQueries are stored as constants
    public static final String QUIZ_CORRECT = "/quiz_correct";
    public static final String QUIZ_INCORRECT = "/quiz_incorrect";
    public static final String QUIZ_START = "/quiz_start";
    public static final String FILL_ORDER = "/fill_order";
    public static final String FILL_ORDER_BROK = "/fill_order_brok";
    // Answer options
    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private final JpaUserRepository userRepository;
    private final JpaQuestionRepository questionRepository;

    public QuizHandler(JpaUserRepository userRepository, JpaQuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (message.startsWith(QUIZ_CORRECT)) {
            // action performed on callback with correct answer
            return correctAnswer(user, message);
        } else if (message.startsWith(QUIZ_INCORRECT)) {
            // action performed on callback with incorrect answer
            return incorrectAnswer(user);
        } else if (message.startsWith(FILL_ORDER)) {
            // action performed on callback with incorrect answer
            return startNewOrder(user);
        }else if (message.startsWith(FILL_ORDER_BROK)) {
            // action performed on callback with incorrect answer
            return fillOrderBrok(user);
        }else {
            return startNewQuiz(user);
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> correctAnswer(User user, String message) {
        log.info("correct");

        // Incrementing user score
        final int currentScore = user.getScore() + 1;
        user.setScore(currentScore);
        userRepository.save(user);

        // Returning next question
        return nextQuestion(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> incorrectAnswer(User user) {
        final int currentScore = user.getScore();
        // Changing high score if needed
        if (user.getHighScore() < currentScore) {
            user.setHighScore(currentScore);
        }
        // Updating user status
        user.setScore(0);
        user.setBotState(State.NONE);
        userRepository.save(user);

        // Creating "Try again?" button
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Try again?", QUIZ_START));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        return List.of(createMessageTemplate(user)
                .setText(String.format("Incorrect!%nYou scored *%d* points!", currentScore))
                .setReplyMarkup(inlineKeyboardMarkup));
    }

    private List<PartialBotApiMethod<? extends Serializable>> startNewQuiz(User user) {
        user.setBotState(State.PLAYING_QUIZ);
        userRepository.save(user);

        return nextQuestion(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> startNewOrder(User user) {
        user.setBotState(State.FILL_ORDER);
        userRepository.save(user);

        return nextQuestionFillOrder(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> fillOrderBrok(User user) {
        SendMessage welcomeMessage = createMessageTemplate(user)
                .setText("Информация о заявке + state: ");

        return List.of(welcomeMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> nextQuestionFillOrder(User user) {
        Question question = questionRepository.getRandomQuestion();

        SendMessage welcomeMessage = createMessageTemplate(user)
                .setText("Заполним информацию о заявке");
        // Asking to input name

        SendMessage registrationMessage = createMessageTemplate(user)
                .setText("Укажи свое ФИО в формате: Иванов Петр Иванович");
        // Changing user state to "awaiting for name input"
        user.setBotState(State.ENTER_NAME);
        userRepository.save(user);
//        user.setBotState(State.FILL_ORDER_BROK);
//        userRepository.save(user);


        return List.of(welcomeMessage, registrationMessage);
    }


    private List<PartialBotApiMethod<? extends Serializable>> nextQuestion(User user) {
        Question question = questionRepository.getRandomQuestion();


        // Getting list of available answers
        List<String> options = new ArrayList<>(List.of(question.getCorrectAnswer(), question.getOptionOne(), question.getOptionTwo(), question.getOptionThree()));
        // Shuffling
        Collections.shuffle(options);

        // Creating message by starting with defining the question
        StringBuilder sb = new StringBuilder();
        sb.append('*')
                .append(question.getQuestion())
                .append("*\n\n");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Create two lines of buttons
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = new ArrayList<>();

        // Define message and add callback data to the buttons
        for (int i = 0; i < options.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();

            final String callbackData = options.get(i).equalsIgnoreCase(question.getCorrectAnswer()) ? QUIZ_CORRECT : QUIZ_INCORRECT;

            button.setText(OPTIONS.get(i))
                    .setCallbackData(String.format("%s %d", callbackData, question.getId()));

            if (i < 2) {
                inlineKeyboardButtonsRowOne.add(button);
            } else {
                inlineKeyboardButtonsRowTwo.add(button);
            }
            sb.append(OPTIONS.get(i) + ". " + options.get(i))
                    .append("\n");
        }

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne, inlineKeyboardButtonsRowTwo));
        return List.of(createMessageTemplate(user)
                .setText(sb.toString())
                .setReplyMarkup(inlineKeyboardMarkup));
    }

    @Override
    public State operatedBotState() {
        return null;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(QUIZ_START, QUIZ_CORRECT, QUIZ_INCORRECT, FILL_ORDER);
    }
}
