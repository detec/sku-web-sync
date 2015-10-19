package com.malbi.sync.sku.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LoginTest extends AbstractTest {

	// private WebDriver driver;
	// private String baseUrl = "http://localhost:8080/sku-web-sync-test";
	// private StringBuffer verificationErrors = new StringBuffer();

	public void doLogin() {
		driver.get(baseUrl + "/");

		// find field username
		WebElement inputField = driver.findElement(By.xpath("//*[@id='LoginForm:username']"));
		assertThat(inputField.isSelected());
		inputField.clear();
		inputField.sendKeys("user");

		// find field password
		WebElement passwordField = driver.findElement(By.xpath("//*[@id='LoginForm:password']"));
		assertThat(passwordField.isSelected());
		passwordField.clear();
		passwordField.sendKeys("malbi39");

		WebElement loginButton = driver.findElement(By.xpath("//*[@id='LoginForm:SubmitButton']"));
		assertThat(loginButton.isSelected());
		loginButton.click();

		// waiting while it processes input
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

		// XLSUploadForm
		WebElement uploadForm = driver.findElement(By.xpath("//*[@id='XLSUploadForm']"));
		assertThat(uploadForm.isSelected());
	}

}
