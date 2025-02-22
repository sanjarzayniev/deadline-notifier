package eclass.kr;

public class Constants {
    public static final String URL = "https://eclass.inha.ac.kr/login.php",
            URL_FOR_WEEKLY_EVENTS = "https://eclass.inha.ac.kr/calendar/view.php",
            DEADLINE_MESSAGE = "Here is the list of deadlines for upcoming 7 days:",
            NO_DEADLINE_MESSAGE = "No deadline. So keep working",
            INVITE_MESSAGE = "Visit this page to check them by yourself: ",
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
    public final static String[] randomEmojis = { "🤲", "🤯", "😈", "😲", "💀", "👀", "🥶", "🤡", "😫", "😳", "🗣" };
}
