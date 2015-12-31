package com.malbi.sync.sku.jsfcontrollers;

import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.SKUGroupChanges;

public class DBSKUUpdateTests extends DBSKUAbstractTest {

	@Test
	public void testShouldFindUpdatedSKU() {
		DBSKUC.refreshData();

		assertEquals(1, DBSKUC.getUpdateList().size());

		assertEquals(0, DBSKUC.getAddList().size());
		assertEquals(0, DBSKUC.getRemoveList().size());

		SKUGroupChanges newElement = DBSKUC.getUpdateList().get(0);
		assertEquals(new DBSKUGroup(301, "Любимов классический", 1020), newElement.getBefore());
		assertEquals(new DBSKUGroup(10, "Драже фас.", 1016), newElement.getAfter());

	}

	@Test
	public void testShouldCommitChanges() {

		DBSKUC.refreshData();
		DBSKUC.getUpdateList().get(0).setChecked(true);

		DBSKUC.applyChanges();

		/*
		 * try { new UnloadTestDatabase(); } catch (ClassNotFoundException |
		 * SQLException | IOException | DatabaseUnitException e) {
		 * e.printStackTrace(); }
		 */

		assertEquals("", DBSKUC.getExceptionString());

		String expectedDatasetPath = "/dbunit/after_changed_sku_hierarchy.xml";
		compareDatasets(expectedDatasetPath);

	}

	@Override
	@Before
	public void setUp() throws Exception {
		XLS_File = "/xls/dbskuupdate.xls";
		XML_File = "/dbunit/db_sku_update.xml";

		super.XLS_File = this.XLS_File;
		super.XML_File = this.XML_File;

		super.setUp();

		loadXLSFile(XLS_File);

	}

	public DBSKUUpdateTests(String name) {
		super(name);
	}

}
