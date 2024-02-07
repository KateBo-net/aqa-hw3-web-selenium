import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ApplicationFormTest {

    WebDriver driver;

    @BeforeAll
    public static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void shouldSendApplication() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79820000000");
        driver.findElement(By.cssSelector("[data-test-id=agreement] span.checkbox__box")).click();
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();

        Assertions.assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", actual.trim());
    }

    @Test
    void shouldSendApplicationAgreementText() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79820000000");
        driver.findElement(By.cssSelector("[data-test-id=agreement] span.checkbox__text")).click();
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();

        Assertions.assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", actual.trim());
    }

    @Test
    void shouldNotPassValidationEmptyFields() throws InterruptedException {
        driver.findElement(By.cssSelector(".form-field button")).click();
        Thread.sleep(500);
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Поле обязательно для заполнения", actual.trim());
        Assertions.assertThrows(NoSuchElementException.class, () -> driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid")));
        Assertions.assertThrows(NoSuchElementException.class, () -> driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")));
    }

    @Test
    void shouldNotPassValidationEmptyPhoneAndCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Поле обязательно для заполнения", actual.trim());
        Assertions.assertThrows(NoSuchElementException.class, () -> driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")));
    }

    @Test
    void shouldNotPassValidationEmptyName() {
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79820000000");
        driver.findElement(By.cssSelector("[data-test-id=agreement] span.checkbox__box")).click();
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Поле обязательно для заполнения", actual.trim());
    }

    @Test
    void shouldNotPassValidationEmptyPhone() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=agreement] span.checkbox__box")).click();
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Поле обязательно для заполнения", actual.trim());
    }

    @Test
    void shouldNotPassValidationEmptyCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79820000000");
        driver.findElement(By.cssSelector(".form-field button")).click();
        driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")).isDisplayed();
    }

    @Test
    void shouldSendApplicationHyphenatedName() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Петрова Эмилия-Анна");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79820000000");
        driver.findElement(By.cssSelector("[data-test-id=agreement] span.checkbox__text")).click();
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();

        Assertions.assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", actual.trim());

    }

    @Test
    void shouldNotPassValidationLatinName() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Bob");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", actual.trim());
    }

    @Test
    void shouldNotPassValidationSpecialCharName() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван(:");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", actual.trim());
    }

    @Test
    void shouldNotPassValidationNumberName() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван77");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", actual.trim());
    }

    @Test
    void shouldNotPassValidationPhoneNumberOfDigitsLess11() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7999112233");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actual.trim());
    }

    @Test
    void shouldNotPassValidationPhoneNumberOfDigitsMore11() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+799911223334");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actual.trim());
    }

    @Test
    void shouldNotPassValidationPhoneWithoutPlus() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("79991122333");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actual.trim());
    }

    @Test
    void shouldNotPassValidationPhonePlusAtTheEnd() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("79991122333+");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actual.trim());
    }

    @Test
    void shouldNotPassValidationPhoneLatin() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("phone79991122333");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actual.trim());
    }

    @Test
    void shouldNotPassValidationPhoneCyrillic() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("тел79991122333");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actual.trim());
    }

    @Test
    void shouldNotPassValidationPhoneSpecialChar() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("-79991122333");
        driver.findElement(By.cssSelector(".form-field button")).click();
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();

        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actual.trim());
    }
}
