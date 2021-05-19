Цель: написать чат бот принимающий информацию от пользователя об участии в soft книге. Действия по скрипту.

State машина

State:
/start - сообщение о согласии о рассылке информации
кнопка принять - (Фикс в бд пользователя(messageId=1141, from=User{id=97942009, firstName='Kir', lastName='null', userName='MrBlender'}) и его согласия)

/find_bookOrder
Проверка в бд наличия новых размещений:
нет: мы сообщим вам о новых размещениях - изменить state пользователя на waiting_order
да: Новое размещение - будешь участвовать?
	да: Заполняем заявку о участии - state: fill_order
	нет: waiting_order

TODO - как-то не очень нравится писать для каждого хендлер и фиксацию в бд только в новом хендлере уже после прихода ответа пользователя, что не удобно для исправлений

/fill_order
Заполнение заявки SmU
ФИО /fill_order_fio
Брокер /fill_order_broker
Кол-во /fill_order_count
Телефон /fill_order_phone
Почта /fill_order_email

/menu
KB - повторить заявку - возврат к /fill_order
KB - редактировать заявку - /edit_order
KB - смотреть размещения - /find_order

/stop (Фикс в бд отказ от accept и удаление всех данных пользователя?)

Действия: 
Sm Send Message - отправка сообщения пользователю
SmU Send Message + update - отправка сообщения пользователю и ожидание действия
IKB InlineKeyboardButton - кнопка с действием под сообщением пользователю
KB - KeyboardButton - кнопка типо меню

Команды
/start
/find_order
/fill_order
/waiting_order
/help
/stop

Админка:
При наборе команды - проверяются права пользователя, колонка в БД админ=true
/new_orderbook - создать новую книгу и разослать ее пользователям в состоянии waiting_order
/statistic - показать статистику по текущей книге
/mail - сделать email рассылку по пользователям заполнившим заявку

БД: таблица пользователи USERS, таблица размещения BOOK_ORDER, таблица заявки ORDERS
USERS 
id_chat, данные от телеграмма: id=97942009, firstName='Kir', lastName='null', userName='MrBlender', accept, state, admin
BOOK_ORDER
id, Name, Description
ORDERS
users_id, book_order_id, id_order, accept_book_order, fio, broker, count, phone, email

TODO - многопоточный запуск, бот должен обрабатывать до 100 пользователей

Касательно разработки, какие в идеи есть полезные штуки для рефакторинга кода?