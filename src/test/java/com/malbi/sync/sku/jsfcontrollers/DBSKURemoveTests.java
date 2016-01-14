package com.malbi.sync.sku.jsfcontrollers;

import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.SKUGroupChanges;

public class DBSKURemoveTests extends DBSKUAbstractTest {

	@Test
	public void testShouldFindSKUToRemove() {
		DBSKUC.refreshData();

		assertEquals(1, DBSKUC.getRemoveList().size());

		assertEquals(0, DBSKUC.getAddList().size());
		assertEquals(0, DBSKUC.getUpdateList().size());

		SKUGroupChanges newElement = DBSKUC.getRemoveList().get(0);
		assertEquals(new DBSKUGroup(301, "Любимов классический", 1020), newElement.getBefore());
		assertEquals(new DBSKUGroup(), newElement.getAfter());

	}

	@Test
	public void testShouldCommitChanges() {
		DBSKUC.refreshData();
		DBSKUC.getRemoveList().get(0).setChecked(true);

		DBSKUC.applyChanges();

		/*
		 * try { new UnloadTestDatabase(); } catch (ClassNotFoundException |
		 * SQLException | IOException | DatabaseUnitException e) {
		 * e.printStackTrace(); }
		 */

		assertEquals("", DBSKUC.getExceptionString());

		String expectedDatasetPath = "/dbunit/after_removed_sku_from_hierarchy.xml";
		compareDatasets(expectedDatasetPath);

	}

	@Override
	@Before
	public void setUp() throws Exception {
		XLS_File = "/xls/dbskuremove.xls";
		XML_File = "/dbunit/db_sku_remove_from_hierarchy.xml";

		super.XLS_File = this.XLS_File;
		super.XML_File = this.XML_File;

		super.setUp();

		loadXLSFile(XLS_File);

	}

	public DBSKURemoveTests(String name) {
		super(name);

	}

}
