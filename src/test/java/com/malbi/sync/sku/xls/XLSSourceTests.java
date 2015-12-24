package com.malbi.sync.sku.xls;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.application.XLSProcessorController;

public class XLSSourceTests extends XLSProcessorAbstractTest {

	public XLSSourceTests(String name) {
		super(name);

	}

	@Override
	@Before
	public void setUp() throws Exception {
		XLS_File = "/xls/xlstester.xls";
		XML_File = "/dbunit/xlstester.xml";

		super.XLS_File = this.XLS_File;
		super.XML_File = this.XML_File;

		super.setUp();

	}

	@Test
	public void testFillXlsSource() throws IOException {

		XLSProcessorController xpc = initData();

		// check that everything validates
		boolean uploadedXLSValidationResult = xSource.validateInternal();
		assertEquals(true, uploadedXLSValidationResult);
		assertEquals(true, xSource.getValidationErrorLog().isEmpty());
	}

}
