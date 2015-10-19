package com.malbi.sync.sku.application;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

// This class actually launches login, upload and walking through pages.
public class LaunchTest extends AbstractTest {

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
