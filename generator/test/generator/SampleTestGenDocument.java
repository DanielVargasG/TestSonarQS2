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


public class SampleTestGenDocument {
	
	WebDriver driver;
	JavascriptExecutor jsExecutor;
	By usernameTextBox = By.id("j_username");
	By passwordTextBox = By.id("j_password");
	By ButtonBox = By.xpath("//html/body/div/div[2]/div/div[2]/form/div[2]/input");
	By ButtonBox2 = By.xpath("//*[@id=\"__xmlview1--searchEmp-I\"]");
	By ButtonBox3 = By.xpath("//*[@id=\"__item0-__clone0-content\"]");
	By ButtonBox4 = By.xpath("//*[@id=\"popoverNavCon--productitem-large-0-content\"]");
	By ButtonBox5 = By.xpath("//*[@id=\"popoverNavCon--idList-listUl\"]");
	By ButtonBox6 = By.xpath("//*[@id=\"popoverNavCon--__button4-content\"]");
	By ButtonBox7 = By.xpath("//*[@id=\"__filter2-icon\"]");
	By ButtonBox8 = By.xpath("//*[@id=\"__filter1-icon\"]");
	By ButtonBox9 = By.xpath("//*[@id=\"__button5-content\"]");
	By ButtonBox10 = By.xpath("//*[@id=\"__button9\"]");
	By ButtonBox11 = By.xpath("//*[@id=\"__button7-content\"]");
	By ButtonBox12 = By.xpath("//*[@id=\"__text0\"]");
	
	
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
		delay(3);
		driver.get("http://18.219.25.112:8080/generator/#/team");
		delay(3);
		WebElement searchField;
		searchField = driver.findElement(ButtonBox2);
		searchField.sendKeys("nsilvani");
		delay(3);
		searchButton = driver.findElement(ButtonBox3);
		searchButton.click();
		delay(3);
		searchButton = driver.findElement(ButtonBox4);
		searchButton.click();
		delay(3);
		searchButton = driver.findElement(ButtonBox5);
		searchButton.click();
		delay(3);
		searchButton = driver.findElement(ButtonBox6);
		searchButton.click();
		delay(10);
		searchButton = driver.findElement(ButtonBox7);
		searchButton.click();
		delay(1);
		searchButton = driver.findElement(ButtonBox8);
		searchButton.click();
		delay(1);
		searchButton = driver.findElement(ButtonBox9);
		searchButton.click();
		delay(2);
		searchButton = driver.findElement(ButtonBox10);
		searchButton.click();
		delay(2);
		searchButton = driver.findElement(ButtonBox11);
		searchButton.click();
		delay(2);
		searchButton = driver.findElement(ButtonBox12);
		searchButton.click();
		delay(2);
		
		
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
