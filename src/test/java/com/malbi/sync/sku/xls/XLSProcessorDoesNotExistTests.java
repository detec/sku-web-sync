package com.malbi.sync.sku.xls;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.application.XLSProcessorController;

public class XLSProcessorDoesNotExistTests extends XLSProcessorAbstractTest {

	public XLSProcessorDoesNotExistTests(String name) {
		super(name);

	}

	@Test
	public void testXLSProceccorCommitChangesRemoveRows() throws IOException {
		XLSProcessorController xpc = initData();
		xpc.getDoesNotExist().get(0).setChecked(true);
		xpc.commitXLSChanges();

		// one row should be in XLS file
		assertEquals(1, xpc.getSessionManager().getxSource().getRows().size());
	}

	@Test
	public void testXLSProcessorDoesNotExist() throws IOException {
		// get test.xls file
		// try with resources
		XLSProcessorController xpc = initData();

		assertEquals(1, xpc.getDoesNotExist().size());
		assertEquals(302204020, xpc.getDoesNotExist().get(0).getId());
		assertEquals(0, xpc.getSkuRename().size());

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

}
