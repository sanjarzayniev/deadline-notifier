package eclass.kr;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DeadlineNotifier {
        public static WebDriver driver;
        public static final String URL = "https://eclass.inha.ac.kr/login.php";
        public static final String DEADLINE_MESSAGE = "We do have some tasks to do. Here is the list of them:";
        public static final String NO_DEADLINE_MESSAGE = "We have no deadline for today. So enjoy!";
        public static final String INVITE_MESSAGE = "You can also visit this page to see the upcoming events by yourself: ";
        public static final String NOT_FOUND_DEADLINE_MESSAGE = "Could not fetch the deadline!";

        public static void main(String[] args) {
                setup();

                login();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions
                                .presenceOfAllElementsLocatedBy(By.cssSelector(".btn.btn-xs.btn-default.btn-more")));

                WebElement moreUpcomingEventsButton = driver
                                .findElement(By.cssSelector(".btn.btn-xs.btn-default.btn-more"));
                moreUpcomingEventsButton.click();

                Integer day = LocalDate.now().getDayOfMonth();
                WebElement date = driver.findElement(By.xpath("//*[text()='" + day.toString() + "']"));
                String linkOfTheEvent = date.getAttribute("href");

                if (date.getTagName().equals("a")) {
                        System.out.println(DEADLINE_MESSAGE);

                        date.click();

                        try {
                                Thread.sleep(5000);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }

                        String typeOfEvent;
                        String nameOfEvent;
                        String nameOfCourse;
                        String timeOfDeadline = "Initial Value";

                        List<WebElement> fieldsOfDeadline;
                        int count = 1;

                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        for (WebElement event : driver.findElements(By.className("card"))) {
                                typeOfEvent = event.findElement(By.tagName("img")).getAttribute("title");
                                nameOfEvent = event.findElement(By.tagName("h3")).findElement(By.tagName("a"))
                                                .getText();

                                nameOfCourse = event.findElement(By.tagName("div")).findElement(By.tagName("div"))
                                                .findElement(By.tagName("a")).getText();
                                fieldsOfDeadline = event.findElement(By.tagName("span")).findElements(By.tagName("a"));

                                if (fieldsOfDeadline.size() == 2) {
                                        timeOfDeadline = /*
                                                          * fieldsOfDeadline.get(1).getText()
                                                          * +
                                                          */event.findElement(By.tagName("span")).getText();
                                } else if (fieldsOfDeadline.size() == 1) {
                                        timeOfDeadline = /*
                                                          * fieldsOfDeadline.get(0).getText()
                                                          * +
                                                          */event.findElement(By.tagName("span")).getText();
                                } else if (fieldsOfDeadline.size() == 0) {
                                        timeOfDeadline = "Today at " + event.findElement(By.tagName("div"))
                                                        .findElement(By.tagName("span"))
                                                        .getText();
                                } else {
                                        timeOfDeadline = NOT_FOUND_DEADLINE_MESSAGE;
                                }

                                if (typeOfEvent.equals("VOD")) {
                                        typeOfEvent = "Video";
                                }

                                

                                System.out.println("\n" + count + ". Course Name: " + nameOfCourse);
                                System.out.println("   Event Name: " + nameOfEvent);
                                System.out.println("   Type: " + typeOfEvent);
                                System.out.println("   Deadline (from >> to): " + timeOfDeadline);

                                count++;

                                js.executeScript("window.scrollBy(0, 650)");
                        }
                        System.out.println("\n" + INVITE_MESSAGE + linkOfTheEvent);

                } else {
                        System.out.println(NO_DEADLINE_MESSAGE);
                }

                // System.out.println("The day of month is " + day.toString());
                // System.out.println("The day of week is " + LocalDate.now().getDayOfWeek());

                tearDown();
        }

        public static void setup() {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                String arguments[] = {
                                "--remote-allow-origins=*",
                                "--no-sandbox",
                                "--disable-dev-shm-usage",
                                // "--headless",
                                // "--remote-debugging-port=9222"
                };
                options.addArguments(arguments);
                driver = new ChromeDriver(options);
        }

        public static void login() {
                driver.get(URL);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                WebElement username = driver.findElement(By.id("input-username"));
                username.sendKeys(System.getenv("ID"));

                WebElement password = driver.findElement(By.id("input-password"));
                password.sendKeys(System.getenv("PASSWORD"));

                WebElement logInButton = driver.findElement(By.cssSelector(".btn.btn-success"));
                logInButton.click();
        }

        public static void tearDown() {
                if (driver != null) {
                        driver.quit();
                }
        }
}
