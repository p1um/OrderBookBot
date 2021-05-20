package ru.bondholders.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.List;

import static ru.bondholders.telegram.bot.handler.QuizHandler.QUIZ_START;
import static ru.bondholders.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;


public class RegistrationHandler implements Handler {
    // Supported CallBackQueries are stored as constants
    public static final String NAME_ACCEPT = "/enter_name_accept";
    public static final String NAME_CHANGE = "/enter_name";
    public static final String NAME_CHANGE_CANCEL = "/enter_name_cancel";

    private final JpaUserRepository userRepository;

    public RegistrationHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Checking type of input message
        if (message.equalsIgnoreCase(NAME_ACCEPT) || message.equalsIgnoreCase(NAME_CHANGE_CANCEL)) {
            return accept(user);
        } else if (message.equalsIgnoreCase(NAME_CHANGE)) {
            return changeName(user);
        }
        return checkName(user, message);

    }

    private List<PartialBotApiMethod<? extends Serializable>> accept(User user) {
        // If user accepted the change - update bot state and save user
        user.setBotState(State.NONE);
        userRepository.save(user);

        // Creating button to start new game
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Start quiz", QUIZ_START));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        return List.of(createMessageTemplate(user).setText(String.format(
                "Your name is saved as: %s", user.getName()))
                .setReplyMarkup(inlineKeyboardMarkup));
    }

    private List<PartialBotApiMethod<? extends Serializable>> checkName(User user, String message) {
        // When we check user name we store it in database immediately
        // refactoring idea: temporal storage
        user.setName(message);
        userRepository.save(user);

        // Creating button to accept changes
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Accept", NAME_ACCEPT));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage welcomeMessage = createMessageTemplate(user)
                .setText("Заполним информацию о брокере");
        // Asking to input name


        SendMessage registrationMessage = createMessageTemplate(user)
                .setText("Укажи своего брокера");
        // Changing user state to "awaiting for name input"
        user.setBotState(State.FILL_ORDER_BROK);
        userRepository.save(user);

        return List.of(welcomeMessage, registrationMessage);}

//        return List.of(createMessageTemplate(user)
//                .setText(String.format("You have entered: %s%nIf this is correct - press the button", user.getName()))
//                .setReplyMarkup(inlineKeyboardMarkup));
//    }

    private List<PartialBotApiMethod<? extends Serializable>> changeName(User user) {
        // When name change request is received - bot state changes
        user.setBotState(State.ENTER_NAME);
        userRepository.save(user);

        SendMessage welcomeMessage = createMessageTemplate(user)
                .setText("Заполним информацию о заявке");
        // Asking to input name


        SendMessage registrationMessage = createMessageTemplate(user)
                .setText("Укажи свое ФИО в формате: Иванов Петр Иванович");
        // Changing user state to "awaiting for name input"
        user.setBotState(State.ENTER_NAME);
        userRepository.save(user);

        // Cancel button creation
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Cancel", NAME_CHANGE_CANCEL));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        return List.of(welcomeMessage, registrationMessage);
    }

    @Override
    public State operatedBotState() {
        return State.ENTER_NAME;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(NAME_ACCEPT, NAME_CHANGE, NAME_CHANGE_CANCEL);
    }
}
