package com.malbi.sync.sku.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.dao.DAO;
import com.malbi.sync.sku.db.DBUnitConfig;
import com.malbi.sync.sku.db.MockConnectionManager;
import com.malbi.sync.sku.db.SQLQueries;
import com.malbi.sync.sku.service.SKUService;

public class XLSSourceTests extends DBUnitConfig {

	public XLSSourceTests(String name) {
		super(name);

	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		// InputStream in =
		// Thread.currentThread().getContextClassLoader().getResourceAsStream("/dbunit/work.xml");

		// 1 item in database, 2 items in test xls file
		InputStream in = getClass().getResourceAsStream("/dbunit/xlstester.xml");
		beforeData = new FlatXmlDataSetBuilder().build(in);

		// from
		// http://www.marcphilipp.de/blog/2012/03/13/database-tests-with-dbunit-part-1/
		tester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);

		tester.setDataSet(beforeData);
		tester.onSetup();
	}

	@Test
	public void testFillXlsSource() throws IOException {

		// get test.xls file
		// try with resources
		File outputFile = null;
		try (InputStream inputStream = getClass().getResourceAsStream("/xls/xlstester.xls");) {
			outputFile = File.createTempFile("SKU_BASE_1C", ".xls");

			try (FileOutputStream outputStream = new FileOutputStream(outputFile);) {
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
		}

		if (outputFile == null) {
			return; // there was an error when creating temp file.
		}

		// prepare mock classes
		MockConnectionManager mockCM = new MockConnectionManager();
		SQLQueries SQLQueriestest = new SQLQueries();

		DAO testDAO = new DAO();
		testDAO.setCm(mockCM);
		testDAO.setSqlQueries(SQLQueriestest);

		SKUService SKUServicetest = new SKUService();
		SKUServicetest.setDAO(testDAO);

		// creating main test class
		XlsxSource xSource = new XlsxSource();
		xSource.setXlsFile(outputFile);
		xSource.setService(SKUServicetest);

		try {
			xSource.initData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// check that everything validates
		boolean uploadedXLSValidationResult = xSource.validateInternal();
		assertEquals(true, uploadedXLSValidationResult);
		assertEquals(true, xSource.getValidationErrorLog().isEmpty());
	}

}
