package com.malbi.sync.sku.xls;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.application.XLSProcessorController;

public class XLSProcessorRenameTests extends XLSProcessorAbstractTest {

	public XLSProcessorRenameTests(String name) {
		super(name);

	}

	@Test
	public void testXLSProceccorCommitChangesRenameRows() throws IOException {
		XLSProcessorController xpc = initData();
		xpc.getSkuRename().get(0).setChecked(true);
		xpc.commitXLSChanges();

		// now should coincide with the name in database
		assertEquals("Шоколад Любимов к/у молочний", xSource.getRows().get(0).getSkuName());
		assertEquals(true, xpc.getExceptionString().isEmpty());

	}

	@Test
	public void testGetRenameListNotEmpty() throws IOException {
		XLSProcessorController xpc = initData();
		assertEquals(1, xpc.getSkuRename().size());
		assertEquals(302204010, xpc.getSkuRename().get(0).getId());
		assertEquals(0, xpc.getDoesNotExist().size());
		assertEquals(true, xpc.getExceptionString().isEmpty());

	}

	@Override
	@Before
	public void setUp() throws Exception {
		XLS_File = "/xls/xlsrename.xls";
		XML_File = "/dbunit/xlstester.xml";

		super.XLS_File = this.XLS_File;
		super.XML_File = this.XML_File;

		super.setUp();

	}

}
