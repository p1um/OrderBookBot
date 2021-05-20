package ru.bondholders.telegram.bot;

// Available bot states
public enum State {
    NONE,
    START,
    FIND_BOOK_ORDER,
    FILL_ORDER_FIO,
    FILL_ORDER_BROK,
    FILL_ORDER_COUNT,
    FILL_ORDER_PHONE,
    FILL_ORDER_EMAIL,
    FILL_ORDER_FINISH,
    ORDER,
    ORDER_REGISTRATION,
    ENTER_NAME,
    FILL_ORDER,

    PLAYING_QUIZ,
}
