package ru.bondholders.telegram.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.bondholders.telegram.bot.State;
import ru.bondholders.telegram.model.User;
import ru.bondholders.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.List;

import static ru.bondholders.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class FillOrderFioHandler implements Handler {
    // Supported CallBackQueries are stored as constants
    public static final String FILL_ORDER_FIO = "/fill_order_fio";

    private final JpaUserRepository userRepository;

    public FillOrderFioHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Checking type of input message

        return fillOrderFio(user, message);

    }


    private List<PartialBotApiMethod<? extends Serializable>> fillOrderFio(User user, String message) {

        SendMessage welcomeMessage = createMessageTemplate(user)
                .setText("Заполним информацию о заявке для участия в книге");
        // Asking to input name

        SendMessage registrationMessage = createMessageTemplate(user)
                .setText("Укажи свое ФИО в формате: Иванов Петр Иванович");
        // Changing user state to "awaiting for name input"
        user.setBotState(State.FILL_ORDER_BROK);
        userRepository.save(user);

        return List.of(welcomeMessage, registrationMessage);
    }


    @Override
    public State operatedBotState() {
        return State.FILL_ORDER_FIO;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(FILL_ORDER_FIO);
    }
}
