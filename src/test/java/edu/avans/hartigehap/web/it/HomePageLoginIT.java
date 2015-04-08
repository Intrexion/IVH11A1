package edu.avans.hartigehap.web.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


@Slf4j
public class HomePageLoginIT {

  public static String URL = "http://localhost:8080/hh";

  @Test
  public void login() {
    WebDriver driver = BrowserUtils.getWebDriver();
    driver.get(URL);
    log.debug("Congratulations, the home page is available ;-) {}", URL);
    WebElement loginDiv = driver.findElement(By.id("login"));
    assertNotNull(loginDiv);
    WebElement nameInput = loginDiv.findElement(By.name("j_username"));
    assertNotNull(nameInput);
    nameInput.sendKeys("employee");
    WebElement passwordInput = loginDiv.findElement(By.name("j_password"));
    passwordInput.sendKeys("employee");
    assertNotNull(passwordInput);

    driver.findElement(By.name("submit")).click();
    try {
      WebElement errorDiv = driver.findElement(By.className("error"));
      fail("For a succesful login, an error div is not expected: " + errorDiv);
    }
    catch (NoSuchElementException ex) {
      log.debug("Login succeeded ;-)");
    }
  }
  
	private WebDriver browser;

	@Before
	public void setup() {
		browser = BrowserUtils.getWebDriver();
	}

	@Test
	public void startTest() {
		browser.get("localhost:8080/hh/reservation?form");
	
		// Will throw exception if elements not found
		browser.findElement(By.id("first-name")).sendKeys("Henk");
		browser.findElement(By.id("last-name")).sendKeys("Jaspers");
		browser.findElement(By.id("email-address")).sendKeys("hhreserveringen@gmail.com");
		browser.findElement(By.id("phone-number")).sendKeys("0123456789");
		browser.findElement(By.id("description")).sendKeys("This is a description");
		
		browser.findElement(By.id("submit")).click();
		
		browser.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		assertEquals("Reserving succeeded", browser.findElement(By.id("reservation-header")).getText());
	}

	@After
	public void tearDown() {
		browser.close();
	}

}
