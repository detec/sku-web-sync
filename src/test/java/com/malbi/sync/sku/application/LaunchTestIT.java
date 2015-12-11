package com.malbi.sync.sku.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

// This class actually launches login, upload and walking through pages.
public class LaunchTestIT extends AbstractTest {

	@Test
	public void launchProcessPath() {
		LoginTest loginTest = new LoginTest();
		loginTest.driver = driver;

		// check login
		loginTest.doLogin();

		XLSUploadTest uploadTest = new XLSUploadTest();
		uploadTest.driver = driver;

		// check xls upload
		uploadTest.doUpload();

		XLSProcessorTest processor = new XLSProcessorTest();
		processor.driver = driver;

		// check xls sku
		processor.doProcess();

		DBGroupsTest DBGroups = new DBGroupsTest();
		DBGroups.driver = driver;

		// check DB groups
		DBGroups.doProcess();

		DBSKUProcessorTest SKU = new DBSKUProcessorTest();
		SKU.driver = driver;

		// check SKU processing
		SKU.doProcess();

		XLSDownloadTest download = new XLSDownloadTest();
		download.driver = driver;

		download.doDownload();
	}

	@Test
	public void checkXLSPorcessorLogout() {

		XLSprocessorLogout();
	}

	@Test
	public void checkSecurityFilter() {

		XLSprocessorLogout();

		driver.get(baseUrl + "/skuprocessor.xhtml");

		// find field username
		WebElement inputField = driver.findElement(By.xpath("//*[@id='j_username']"));
		assertThat(inputField.isSelected());
	}

	private void XLSprocessorLogout() {
		LoginTest loginTest = new LoginTest();
		loginTest.driver = driver;

		// check login
		loginTest.doLogin();

		XLSUploadTest uploadTest = new XLSUploadTest();
		uploadTest.driver = driver;

		// check xls upload
		uploadTest.doUpload();

		XLSProcessorTest processor = new XLSProcessorTest();
		processor.driver = driver;

		// finish session
		processor.doLogout();

	}

	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

}
