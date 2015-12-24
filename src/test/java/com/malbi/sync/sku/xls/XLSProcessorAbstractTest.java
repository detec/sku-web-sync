package com.malbi.sync.sku.xls;

import java.io.IOException;
import java.io.InputStream;

import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import com.malbi.sync.sku.application.XLSProcessorController;
import com.malbi.sync.sku.db.DBUnitConfig;

public abstract class XLSProcessorAbstractTest extends DBUnitConfig {

	public String XLS_File = "";

	public String XML_File = "";

	// to prevent possible errors let's construct with parameters
	public XLSProcessorAbstractTest(String XLS_File, String XML_File) {
		this.XLS_File = XLS_File;
		this.XML_File = XML_File;
	}

	public XLSProcessorAbstractTest(String name) {
		super(name);

	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		// InputStream in =
		// Thread.currentThread().getContextClassLoader().getResourceAsStream("/dbunit/work.xml");

		// 1 item in database, 2 items in test xls file
		InputStream in = getClass().getResourceAsStream(XML_File);
		beforeData = new FlatXmlDataSetBuilder().build(in);

		// from
		// http://www.marcphilipp.de/blog/2012/03/13/database-tests-with-dbunit-part-1/
		tester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);

		tester.setDataSet(beforeData);
		tester.onSetup();

	}

	public XLSProcessorController initData() throws IOException {
		loadXLSFile(XLS_File);

		XLSProcessorController xpc = new XLSProcessorController();

		xpc.setService(SKUServiceTest);
		xpc.setSessionManager(mockSessionManager);
		xpc.refreshData();

		return xpc;
	}

	@Override
	public DatabaseOperation getTearDownOperation() throws Exception {
		return super.getTearDownOperation();
	}

}
