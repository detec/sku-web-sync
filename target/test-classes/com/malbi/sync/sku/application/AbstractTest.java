package com.malbi.sync.sku.application;

import org.openqa.selenium.WebDriver;

public abstract class AbstractTest {

	public WebDriver driver;
	public String baseUrl = "http://localhost:8080/sku-web-sync-test";
	public StringBuffer verificationErrors = new StringBuffer();

	public void fail(String exceptionString) throws Exception {
		throw new Exception(exceptionString);
	}

}
