package com.malbi.sync.sku.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class XLSDownloadTest extends AbstractTest {

	public void doDownload() {
		WebElement applyButton = driver.findElement(By.id("XLSDownloadForm:DownloadButton"));
		assertThat(applyButton.isSelected());
		applyButton.click();
	}

	public void doLogout() {

		WebElement applyButton = driver.findElement(By.id("XLSDownloadForm:logoutButton"));
		assertThat(applyButton.isSelected());
		applyButton.click();

	}

}
