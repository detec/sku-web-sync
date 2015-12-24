package com.malbi.sync.sku.jsfcontrollers;

import java.io.InputStream;

import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import com.malbi.sync.sku.application.DBSKUGroupsController;
import com.malbi.sync.sku.db.DBUnitConfig;

public class DBSKUGroupsAbstractTest extends DBUnitConfig {

	public String XLS_File = "";

	public String XML_File = "";

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

		DBskuGC.setService(SKUServiceTest);
		DBskuGC.setSessionManager(mockSessionManager);

	}

	@Override
	public DatabaseOperation getTearDownOperation() throws Exception {
		return super.getTearDownOperation();
	}

	public DBSKUGroupsController DBskuGC = new DBSKUGroupsController();

	public DBSKUGroupsAbstractTest(String name) {
		super(name);

	}

}
