package com.malbi.sync.sku.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class XLSUploadTest extends AbstractTest {

	// it is supposed that we have already logged in
	public void doUpload() {

		// find field username
		WebElement inputField = driver.findElement(By.xpath("//*[@id='XLSUploadForm:file']"));
		assertThat(inputField.isSelected());

		File file = null;

		ClassLoader classLoader = this.getClass().getClassLoader();
		URL resource = classLoader.getResource("test.xls");
		file = new File(resource.getFile());

		assertThat(file);
		// print file name
		inputField.sendKeys(file.getAbsolutePath());

		driver.manage().timeouts().implicitlyWait(12, TimeUnit.SECONDS);

		// upload file to server
		WebElement browseButton = driver.findElement(By.id("XLSUploadForm:uploadButton"));
		assertThat(browseButton.isSelected());
		browseButton.click();

		// waiting while it processes input
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);

		WebElement processButton = driver.findElement(By.id("XLSUploadForm:processButton"));
		assertThat(processButton.isDisplayed());
		assertThat(processButton.isSelected());

		processButton.click();

		// waiting while it processes input, it looks into database
		driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);

		WebElement xlsProcessor = driver.findElement(By.id("XLSProcessorForm"));
		assertThat(xlsProcessor.isSelected());

	}

	public void doLogout() {

		WebElement applyButton = driver.findElement(By.id("XLSUploadForm:logoutButton"));
		assertThat(applyButton.isSelected());
		applyButton.click();

	}

}
