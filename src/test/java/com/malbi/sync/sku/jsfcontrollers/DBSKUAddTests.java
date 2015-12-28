package com.malbi.sync.sku.jsfcontrollers;

import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.SKUGroupChanges;

public class DBSKUAddTests extends DBSKUAbstractTest {

	@Test
	public void testShouldFindAddedSKU() {
		DBSKUC.refreshData();

		assertEquals(1, DBSKUC.getAddList().size());

		assertEquals(0, DBSKUC.getUpdateList().size());
		assertEquals(0, DBSKUC.getRemoveList().size());

		SKUGroupChanges newElement = DBSKUC.getAddList().get(0);
		assertEquals(new DBSKUGroup(), newElement.getBefore());
		assertEquals("Любимов классический", newElement.getAfter().getName());

	}

	@Test
	public void testShouldCommitChanges() {
		DBSKUC.refreshData();

		DBSKUC.getAddList().get(0).setChecked(true);
		DBSKUC.applyChanges();

		/*
		 * try { new UnloadTestDatabase(); } catch (ClassNotFoundException |
		 * SQLException | IOException | DatabaseUnitException e) {
		 * e.printStackTrace(); }
		 */

		assertEquals("", DBSKUC.getExceptionString());

		String expectedDatasetPath = "/dbunit/after_added_new_sku_in_hierarchy.xml";
		compareDatasets(expectedDatasetPath);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		XLS_File = "/xls/dbskuadd.xls";
		XML_File = "/dbunit/db_sku_add.xml";

		super.XLS_File = this.XLS_File;
		super.XML_File = this.XML_File;

		super.setUp();

		loadXLSFile(XLS_File);

	}

	public DBSKUAddTests(String name) {
		super(name);
	}

}
