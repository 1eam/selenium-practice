package org.example.seleniumpractise;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This testclass tests the following website: https://www.selenium.dev for its expected behavior
 */
public class SeleniumPractisesTest {
    private WebDriver driver;
    private static final String HOMEPAGE_URL = "https://www.selenium.dev/";
    private static final String DOCUMENTATION_URL = "https://www.selenium.dev/documentation/";

    @BeforeEach
    void setup() {
        driver = new ChromeDriver(new ChromeOptions().addArguments("--disable-search-engine-choice-screen"));
    }

    @AfterEach
    void tearDown() {
        //Info: driver.close() sometimes fails for Chrome, hence why driver.quit is used instead
        driver.quit();
    }

    @Test
    void homepageUrlTest() {
        driver.get(HOMEPAGE_URL);
        assertEquals(HOMEPAGE_URL, driver.getCurrentUrl());
    }

    @Test
    void webpageTitleTest() {
        driver.navigate().to(HOMEPAGE_URL);
        assertEquals("Selenium", driver.getTitle());
    }

    @Test
    @DisplayName("Clicking on \"Documentation\" from the homepage navigates to the expected url")
    void userClicksOnDocumentationTabTest() {
        driver.navigate().to(HOMEPAGE_URL);
        driver.findElement(By.linkText("Documentation"))
                .click();

        assertEquals(DOCUMENTATION_URL, driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Clicking on \"Search\" from the \"Documentations\" page opens a search popup")
    void userClicksOnSearchTest() {
        String firstSearchbar = "DocSearch-Button-Placeholder";
        String popupClass = "DocSearch-Modal";

        driver.navigate().to(DOCUMENTATION_URL);
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.className(popupClass)));

        driver.findElement(By.className(firstSearchbar)).click();
        assertTrue(driver.findElement(By.className(popupClass)).isDisplayed());
    }

    @Test
    @DisplayName("Typing in searchbar shows results")
    void userTypesInputInSearchbarTest() throws InterruptedException {
        String firstSearchbar = "DocSearch-Button-Container";
        String secondSearchbar = "docsearch-input";
        String searchResultsDropdown = "DocSearch-Dropdown-Container";

        driver.navigate().to(DOCUMENTATION_URL);
        driver.findElement(By.className(firstSearchbar)).click();
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.className(searchResultsDropdown)));

        driver.findElement(By.id(secondSearchbar)).sendKeys("Locators");
        waitOneSecond(); //info: needs time to search for results before hitting enter. The time to wait depends on client internet connection and server response time. It is a flaky test because of this.
        assertTrue(driver.findElement(By.className(searchResultsDropdown)).isDisplayed());
    }

    @Test
    @DisplayName("Clicking on search result navigates to corresponding page")
    void userClicksOnSearchResultTest() throws InterruptedException {
        String firstSearchbar = "DocSearch-Button-Container";
        String secondSearchbar = "docsearch-input";
        String searchResultEnterButton = "DocSearch-Hit-Select-Icon";

        driver.navigate().to(DOCUMENTATION_URL);
        driver.findElement(By.className(firstSearchbar)).click();
        driver.findElement(By.id(secondSearchbar)).sendKeys("Locators");
        waitOneSecond(); //info: needs time to search for results before hitting enter. The time to wait depends on client internet connection and server response time. It is a flaky test because of this.
        driver.findElement(By.className(searchResultEnterButton)).click();

        assertEquals("https://www.selenium.dev/documentation/webdriver/elements/locators/", driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Pressing enter after typing in searchbar navigates to corresponing page (first hit)")
    void userPressesEnterAfterSearchTest() throws InterruptedException {
        String firstSearchbar = "DocSearch-Button-Container";
        String secondSearchbar = "docsearch-input";

        driver.navigate().to(DOCUMENTATION_URL);
        driver.findElement(By.className(firstSearchbar)).click();
        driver.findElement(By.id(secondSearchbar)).sendKeys("Locators");
        waitOneSecond(); //info: needs time to search for results before hitting enter. The time to wait depends on client internet connection and server response time. It is a flaky test because of this.
        driver.findElement(By.id(secondSearchbar)).sendKeys(Keys.ENTER);

        assertEquals("https://www.selenium.dev/documentation/webdriver/elements/locators/", driver.getCurrentUrl());
    }

    @Test
    @DisplayName("User clicks on \"Edit this page\" opens new tab to github")
    void userClicksOnEditPageTest() {
        String linkText = ("Edit this page");

        driver.manage().window().maximize();
        driver.navigate().to(DOCUMENTATION_URL);
        assertEquals(1, driver.getWindowHandles().size());

//        waitOneSecond(); //info: Sometimes time is needed to find the element on the page. It is a flaky test because of this.
        driver.findElement(By.partialLinkText(linkText)).click();
//        waitOneSecond(); //info: Sometimes time is needed to trigger the new tab to open. It is a flaky test because of this.
        assertEquals(2, driver.getWindowHandles().size());

        ArrayList<String> tabs = new ArrayList(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        assertTrue(driver.getCurrentUrl().startsWith("https://github.com/"));
    }

    private static void waitOneSecond() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }
}
