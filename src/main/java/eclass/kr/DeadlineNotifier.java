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

    public static void main(String[] args) {
        setup();

        login();

        action(); // welcome to mess

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
        driver.get(Constants.URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebElement username = driver.findElement(By.id(Constants.USERNAME_SELECTOR));
        username.sendKeys(Constants.ID);

        WebElement password = driver.findElement(By.id(Constants.PASSWORD_SELECTOR));
        password.sendKeys(Constants.PASSWORD);

        WebElement logInButton = driver.findElement(By.cssSelector(Constants.LOGIN_BUTTON_SELECTOR));
        logInButton.click();
    }

    public static void action() {

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

        driver.get(Constants.URL_FOR_WEEKLY_EVENTS);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        printTodaysDate();

        if (!driver.getPageSource().contains(Constants.NO_EVENT_MESSAGE)) {
            sleep(5);

            String nameOfCourse = Constants.DEFAULT_VALUE,
                    nameOfEvent = Constants.DEFAULT_VALUE,
                    typeOfEvent = Constants.DEFAULT_VALUE,
                    timeOfDeadline = Constants.DEFAULT_VALUE;

            JavascriptExecutor js = (JavascriptExecutor) driver;
            boolean isDeadlineMessageCalled = false;
            int count = 1; // index for for-each loop and will be to print the order

            for (WebElement event : driver.findElements(By.className("card"))) {
                if (!event.findElement(By.tagName("img")).getAttribute("alt").equals("Course event")
                        && !event.findElement(By.tagName("span")).getText().startsWith("Today, 00:")) {
                    if (!isDeadlineMessageCalled) {
                        System.out.println("\n" + Constants.DEADLINE_MESSAGE);
                        isDeadlineMessageCalled = true;
                    }
                    typeOfEvent = event.findElement(By.tagName("img")).getAttribute("title");
                    nameOfEvent = event.findElement(By.tagName("h3")).findElement(By.tagName("a"))
                            .getText();

                    nameOfCourse = event.findElement(By.tagName("div"))
                            .findElement(By.tagName("div"))
                            .findElement(By.tagName("a")).getText();

                    timeOfDeadline = parseDeadline(timeOfDeadline, event);

                    if (typeOfEvent.equals(Constants.VOD)) {
                        typeOfEvent = "Video";
                    }

                    nameOfCourse = ignoreAmpersand(nameOfCourse);
                    nameOfEvent = ignoreAmpersand(nameOfEvent);

                    printInfo(count, nameOfCourse, nameOfEvent, typeOfEvent, timeOfDeadline);
                    count++;
                    js.executeScript(Constants.JS_SCRIPT); // scroll down by 550 pixels vertically
                } else {
                    continue;
                }
            }

            if (nameOfCourse.equals(Constants.DEFAULT_VALUE) &&
                    nameOfEvent.equals(Constants.DEFAULT_VALUE) &&
                    typeOfEvent.equals(Constants.DEFAULT_VALUE) &&
                    timeOfDeadline.equals(Constants.DEFAULT_VALUE)) {
                printNoDeadlineMessage();
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

    public static void printTodaysDate() {
        LocalDate today = LocalDate.now();

        System.out.println("Today is " + today.getMonth() + " " + today.getDayOfMonth()
                + ", " + today.getYear());
    }

    public static String parseDeadline(String timeOfDeadline, WebElement event) {

        timeOfDeadline = event.findElement(By.tagName("span")).getText();

        if (timeOfDeadline.contains("»")) {
            String[] deadlineParts = timeOfDeadline.split("»");
            timeOfDeadline = deadlineParts[1].trim(); // take the second part after »
        }

        if (timeOfDeadline.startsWith("Tomorrow")) {
            if (timeOfDeadline.split(",")[1].startsWith(" 00:")) {
                timeOfDeadline = timeOfDeadline.replace("Tomorrow,", "At the next");
                timeOfDeadline = timeOfDeadline + " 🔴";
            } else {
                timeOfDeadline = timeOfDeadline.replace(",", " at");
                timeOfDeadline = timeOfDeadline + " 🟡";
            }
        } else if (timeOfDeadline.startsWith("Today")) {
            String[] parts = timeOfDeadline.split(",");
            timeOfDeadline = parts[0] + " at" + parts[1] + " 🔴";
        } else {
            String[] parts = timeOfDeadline.split(",");
            timeOfDeadline = parts[0] + "," + parts[1] + " at" + parts[2] + " 🟢";
        }

        return timeOfDeadline;
    }

    /*
     * specific edge case for OS & SP & DAD courses (Thank you, prof. Naseer and
     * Agoustini)
     */
    public static String ignoreAmpersand(String attribute) {
        if (attribute.contains("&")) {
            String[] parts = attribute.split("&");
            attribute = "";
            for (int i = 0; i < parts.length; i++) {
                if (i == parts.length - 1) {
                    attribute = attribute
                            + parts[i].trim();
                } else {
                    attribute = attribute + parts[i].trim()
                            + " ";
                }
            }
        }

        return attribute;
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
        System.out.println("\n" + Constants.NO_DEADLINE_MESSAGE);
    }

    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
