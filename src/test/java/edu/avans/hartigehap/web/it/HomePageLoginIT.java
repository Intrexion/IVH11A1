package edu.avans.hartigehap.web.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


@Slf4j
public class HomePageLoginIT {

  @Test
  public void login() {
	String URL = "http://localhost:8080/hh";
    WebDriver driver = BrowserUtils.getWebDriver();
    driver.get(URL);
    driver.findElement(By.name("j_username")).sendKeys("employee");
	driver.findElement(By.name("j_password")).sendKeys("employee");;
    
    driver.findElement(By.name("submit")).click();
	driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    try {
      WebElement errorDiv = driver.findElement(By.className("error"));
      fail("For a succesful login, an error div is not expected: " + errorDiv);
    }
    catch (NoSuchElementException ex) {
      log.debug("Login succeeded ;-)");
    }
    driver.findElement(By.linkText("Logout")).click();
  }
  
  @Test
  public void deleteReservation() {
	  WebDriver driver = BrowserUtils.getWebDriver();
	  String URL = "http://localhost:8080/hh";
	  driver.get(URL);
	
	  driver.findElement(By.name("j_username")).sendKeys("employee");
	  driver.findElement(By.name("j_password")).sendKeys("employee");
		
	  driver.findElement(By.name("submit")).click();	
	  driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
	  driver.findElement(By.linkText("Reservations")).click();
	  driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

	  driver.findElement(By.xpath("//button[contains(.,'Delete')]")).click();
	  driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
	
	  try {
		  WebElement error = driver.findElement(By.linkText("bakker"));
		  fail("For a succesful delete, the bakker link is not expected: " + error);
	  }
	  catch (NoSuchElementException ex) {
		  log.debug("Reseration deleted");
	  }
	  driver.findElement(By.linkText("Logout")).click();
  }
  
  @Test
  public void createReservationSuccess() {
	  WebDriver driver = BrowserUtils.getWebDriver();
	  String URL = "http://localhost:8080/hh/reservation?form";
	  driver.get(URL);
	  
	  driver.findElement(By.id("first-name")).sendKeys("Henk");
	  driver.findElement(By.id("last-name")).sendKeys("Jaspers");
	  driver.findElement(By.id("email-address")).sendKeys("hhreserveringen@gmail.com");
	  driver.findElement(By.id("phone-number")).sendKeys("0123456789");
	  driver.findElement(By.id("description")).sendKeys("This is a description");
		
	  driver.findElement(By.id("submit")).click();
		
	  driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
	  assertEquals("Reserving succeeded", driver.findElement(By.id("reservation-header")).getText());
  }
  
  @Test
  public void createReservationFailed() {
	  WebDriver driver = BrowserUtils.getWebDriver();
	  String URL = "http://localhost:8080/hh/reservation?form";
	  driver.get(URL);

	  driver.findElement(By.id("party-size")).sendKeys("\b");
	  driver.findElement(By.id("party-size")).sendKeys("12");
	  driver.findElement(By.id("first-name")).sendKeys("Henk");
	  driver.findElement(By.id("last-name")).sendKeys("Jaspers");
	  driver.findElement(By.id("email-address")).sendKeys("hhreserveringen@gmail.com");
	  driver.findElement(By.id("phone-number")).sendKeys("0123456789");
	  driver.findElement(By.id("description")).sendKeys("This is a description");
		
	  driver.findElement(By.id("submit")).click();
		
	  driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
	  assertEquals("Reserving failed", driver.findElement(By.id("reservation-header")).getText());
  }  
}