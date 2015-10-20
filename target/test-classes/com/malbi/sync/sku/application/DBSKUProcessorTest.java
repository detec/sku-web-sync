package com.malbi.sync.sku.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DBSKUProcessorTest extends AbstractTest {

	public void doProcess() {

		WebElement applyButton = driver.findElement(By.id("SKUForm:ProcessNext"));
		assertThat(applyButton.isSelected());
		applyButton.click();

		// waiting while it processes input
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);

		waitNextPage();
	}

	public void doSkip() {

		WebElement applyButton = driver.findElement(By.id("SKUForm:goNext"));
		assertThat(applyButton.isSelected());
		applyButton.click();

		// waiting while it processes input
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);

		waitNextPage();
	}

	public void doLogout() {

		WebElement applyButton = driver.findElement(By.id("SKUForm:logoutButton"));
		assertThat(applyButton.isSelected());
		applyButton.click();

	}

	private void waitNextPage() {

		WebElement xlsProcessor = driver.findElement(By.id("XLSDownloadForm"));
		assertThat(xlsProcessor.isSelected());
	}

}
