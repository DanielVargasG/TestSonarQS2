package generator;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import junit.framework.Assert;

public class SampleTestCreateUser {

	WebDriver driver;
	JavascriptExecutor jsExecutor; 
	By usernameTextBox = By.id("j_username");
	By passwordTextBox = By.id("j_password");
	By ButtonBox = By.xpath("//html/body/div/div[2]/div/div[2]/form/div[2]/input");
	By expandButton = By.xpath("//*[@id=\"__panel3-CollapsedImg\"]");
	By expandButton2 = By.xpath("//*[@id=\"__xmlview1--eventId-arrow\"]");
	By eventNameButton = By.xpath("//*[@id=\"__item5-__xmlview1--eventId-2\"]");
	By userIdButton = By.name("userIdValue");
	By userDateButton = By.xpath("//*[@id=\"__xmlview1--dateEffEvent-inner\"]");
	By useSeqButton = By.name("userSeq");
	By generateEventButton = By.xpath("//*[@id=\"__button4-inner\"]");
	By generatedEventTxt = By.xpath("//html/body/div[1]/div[4]");

	@Before
	public void setUpTest() {
		System.setProperty("webdriver.gecko.driver", "C:\\geckodriver\\geckodriver.exe");
		driver = new FirefoxDriver();
		jsExecutor = (JavascriptExecutor) driver;

	} 

	@Test
	public void firstTest() {

		driver.get("http://18.219.25.112:8080/generator/");
		String typeKeywordJS = "document.getElementById('j_username').value='mtuitel'";
		String typeKeywordJS2 = "document.getElementById('j_password').value='abc'";
		jsExecutor.executeScript(typeKeywordJS);
		jsExecutor.executeScript(typeKeywordJS2);
		WebElement searchButton;
		searchButton = driver.findElement(ButtonBox);
		searchButton.click();
		delay(5);
		waitUntilPageLoaded();
		driver.get("http://18.219.25.112:8080/generator/#/admControlPanel");
		delay(5);
		waitUntilPageLoaded();
		WebElement searchButton2;
		searchButton = driver.findElement(expandButton);
		searchButton.click();
		WebElement searchButton3;
		searchButton = driver.findElement(expandButton2);
		searchButton.click();
		WebElement searchButton4;
		searchButton = driver.findElement(eventNameButton);
		searchButton.click();
		WebElement searchField;
		searchField = driver.findElement(userIdButton);
		searchField.sendKeys("nsilvani");
		WebElement searchField2;
		searchField2 = driver.findElement(userDateButton);
		searchField2.sendKeys("2019-04-09");
		WebElement searchField3;
		searchField3 = driver.findElement(useSeqButton);
		searchField3.sendKeys("1");
		WebElement searchButton5;
		searchButton = driver.findElement(generateEventButton);
		searchButton.click();
		WebElement searchButton6;
		searchButton = driver.findElement(generatedEventTxt);
		searchButton.getText();
		
	}

	public void tearDownTest() {
		driver.quit();
	}

	private void delay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void waitUntilPageLoaded() {
		Boolean isLoaded = false;
		while (!isLoaded) {
			isLoaded = isPageLoaded();
			delay(1);
		}
	}

	private Boolean isPageLoaded() {
		String jsQuery = "function pageLoaded() " + "{var loadingStatus=(document.readyState=='complete');"
				+ "return loadingStatus;};" + "return pageLoaded()";

		return (Boolean) jsExecutor.executeScript(jsQuery);
	}

}
