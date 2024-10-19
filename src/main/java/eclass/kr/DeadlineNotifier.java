package eclass.kr;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DeadlineNotifier {
        public static WebDriver driver;
        public static final String URL = "https://eclass.inha.ac.kr/login.php";
        public static final String URL_FOR_TODAYS_EVENTS = "https://eclass.inha.ac.kr/calendar/view.php?view=day";
        public static final String DEADLINE_MESSAGE = "We do have some tasks to do. Here is the list of them:";
        public static final String NO_DEADLINE_MESSAGE = "We have no deadline for today. So enjoy and chill!";
        public static final String INVITE_MESSAGE = "You can also visit this page to see the upcoming events by yourself: ";
        public static final String NOT_FOUND_DEADLINE_MESSAGE = "Could not fetch the deadline!";
        public static final String VOD = "VOD";
        public static final String NO_EVENT_MESSAGE = "There are no events this day.";
        public static final String DEFAULT_VALUE = "NOT FOUND";
        public static final String USERNAME_SELECTOR = "input-username";
        public static final String PASSWORD_SELECTOR = "input-password";
        public static final String LOGIN_BUTTON_SELECTOR = ".btn.btn-success";
        public static final String JS_SCRIPT = "window.scrollBy(0, 550)";
        public static final int MIN_LENGTH_THAT_DEADLINE_CAN_BE = 5;

        public static void main(String[] args) {
                setup();

                login();

                action();

                tearDown();

                System.out.println("Bye-Bye...Mew");
        }

        public static void setup() {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                String arguments[] = {
                                "--remote-allow-origins=*",
                                "--no-sandbox",
                                "--disable-dev-shm-usage",
                                "--headless",
                                "--remote-debugging-port=9222"
                };
                options.addArguments(arguments);
                driver = new ChromeDriver(options);
        }

        public static void login() {
                driver.get(URL);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                WebElement username = driver.findElement(By.id(USERNAME_SELECTOR));
                username.sendKeys(System.getenv("ID"));

                WebElement password = driver.findElement(By.id(PASSWORD_SELECTOR));
                password.sendKeys(System.getenv("PASSWORD"));

                WebElement logInButton = driver.findElement(By.cssSelector(LOGIN_BUTTON_SELECTOR));
                logInButton.click();
        }

        public static void action() { // welcome to the mess

                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

                driver.get(URL_FOR_TODAYS_EVENTS);

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

                printDate(); // today's date, of course

                if (!driver.getPageSource().contains(NO_EVENT_MESSAGE)) {
                        sleep(5);

                        String nameOfCourse = DEFAULT_VALUE;
                        String nameOfEvent = DEFAULT_VALUE;
                        String typeOfEvent = DEFAULT_VALUE;
                        String timeOfDeadline = DEFAULT_VALUE;

                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        boolean isDeadlineMessageCalled = false;
                        int count = 1; // index for for-each loop

                        for (WebElement event : driver.findElements(By.className("card"))) {

                                if (!event.findElement(By.tagName("img")).getAttribute("alt").equals("Course event")) {
                                        if (!isDeadlineMessageCalled) {
                                                System.out.println("\n" + DEADLINE_MESSAGE);
                                                isDeadlineMessageCalled = true;
                                        }
                                        typeOfEvent = event.findElement(By.tagName("img")).getAttribute("title");
                                        nameOfEvent = event.findElement(By.tagName("h3")).findElement(By.tagName("a"))
                                                        .getText();

                                        nameOfCourse = event.findElement(By.tagName("div"))
                                                        .findElement(By.tagName("div"))
                                                        .findElement(By.tagName("a")).getText();

                                        timeOfDeadline = parseDeadline(timeOfDeadline, event);
                                        if (typeOfEvent.equals(VOD)) {
                                                typeOfEvent = "Video";
                                        }

                                        printInfo(count, nameOfCourse, nameOfEvent, typeOfEvent, timeOfDeadline);
                                        count++;
                                        js.executeScript(JS_SCRIPT); // scroll down by 550 pixels vertically
                                } else {
                                        continue;
                                }
                        }

                        if (nameOfCourse.equals(DEFAULT_VALUE) &&
                                        nameOfEvent.equals(DEFAULT_VALUE) &&
                                        typeOfEvent.equals(DEFAULT_VALUE) &&
                                        timeOfDeadline.equals(DEFAULT_VALUE)) {
                                printNoDeadlineMessage();
                        } else {
                                System.out.println(
                                                "\n" + INVITE_MESSAGE + URL_FOR_TODAYS_EVENTS);
                        }
                } else {
                        printNoDeadlineMessage();
                }
        }

        public static void sleep(int seconds) {
                try {
                        Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
        }

        public static void printDate() {
                System.out.println("Today is " + LocalDate.now().getMonth() + " " + LocalDate.now().getDayOfMonth()
                                + ", " + LocalDate.now().getYear());
        }

        public static String parseDeadline(String timeOfDeadline, WebElement event) {

                timeOfDeadline = event.findElement(By.tagName("span")).getText();

                if (timeOfDeadline.contains("»")) {
                        String[] deadlineParts = timeOfDeadline.split("»");
                        timeOfDeadline = deadlineParts[1].trim(); // take the second part after »
                }

                if (timeOfDeadline.length() == MIN_LENGTH_THAT_DEADLINE_CAN_BE) {
                        timeOfDeadline = "Today at " + timeOfDeadline;
                }

                if (timeOfDeadline.startsWith("Today")) {
                        timeOfDeadline = timeOfDeadline + " ⚠️";
                }

                return timeOfDeadline;
        }

        public static void printInfo(int count,
                        String nameOfCourse,
                        String nameOfEvent,
                        String typeOfEvent,
                        String timeOfDeadline) {
                System.out.println("\n" + count + ". Course Name: " + nameOfCourse);
                System.out.println("    Event Name: " + nameOfEvent);
                System.out.println("    Type: " + typeOfEvent);
                System.out.println("    Deadline: " + timeOfDeadline);
        }

        public static void printNoDeadlineMessage() {
                System.out.println("\n" + NO_DEADLINE_MESSAGE);
        }

        public static void tearDown() {
                if (driver != null) {
                        driver.quit();
                }
        }
}