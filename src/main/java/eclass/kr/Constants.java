package eclass.kr;

public class Constants {
    public static final String URL = "https://eclass.inha.ac.kr/login.php",
            URL_FOR_TODAYS_EVENTS = "https://eclass.inha.ac.kr/calendar/view.php?view=day",
            DEADLINE_MESSAGE = "We do have some tasks to do. Here is the list of them:",
            NO_DEADLINE_MESSAGE = "We have no deadline for today. So enjoy and chill!",
            INVITE_MESSAGE = "You can also visit this page to see the upcoming events by yourself: ",
            NOT_FOUND_DEADLINE_MESSAGE = "Could not fetch the deadline!",
            VOD = "VOD",
            NO_EVENT_MESSAGE = "There are no events this day.",
            DEFAULT_VALUE = "NOT FOUND",
            USERNAME_SELECTOR = "input-username",
            PASSWORD_SELECTOR = "input-password",
            LOGIN_BUTTON_SELECTOR = ".btn.btn-success",
            JS_SCRIPT = "window.scrollBy(0, 550)",
            ID = System.getenv("ID"),
            PASSWORD = System.getenv("PASSWORD");
    public static final int MIN_LENGTH_THAT_DEADLINE_CAN_BE = 5;
}