package edu.avans.hartigehap.web.it;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import edu.avans.hartigehap.domain.Customer;
import edu.avans.hartigehap.domain.Reservation;

public class DeleteReservationFrontEnd {
	
	private WebDriver browser;

	@Before
	public void setup() {
		browser = new FirefoxDriver();
	}

	@Test
	public void startTest() throws InterruptedException {
		browser.get("localhost:8080/hh/");
	
		// Will throw exception if elements not found
		browser.findElement(By.name("j_username")).sendKeys("employee");
		browser.findElement(By.name("j_password")).sendKeys("employee");
		
		browser.findElement(By.name("submit")).click();	
		browser.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		browser.findElement(By.linkText("Reservations")).click();
		browser.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

		browser.findElement(By.xpath("//button[contains(.,'Delete')]")).click();
		browser.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		Boolean isPresent = browser.findElements(By.linkText("bakker")).size() > 0;
		assertEquals(false, isPresent);
	}

	@After
	public void tearDown() {
		browser.close();
	}
}
