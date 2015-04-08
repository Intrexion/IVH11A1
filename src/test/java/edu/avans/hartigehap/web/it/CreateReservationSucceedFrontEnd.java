package edu.avans.hartigehap.web.it;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateReservationSucceedFrontEnd {
	
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
