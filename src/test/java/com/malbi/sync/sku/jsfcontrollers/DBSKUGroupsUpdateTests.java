package com.malbi.sync.sku.jsfcontrollers;

import org.junit.Before;
import org.junit.Test;

public class DBSKUGroupsUpdateTests extends DBSKUGroupsAbstractTest {

	public DBSKUGroupsUpdateTests(String name) {
		super(name);

	}

	@Override
	@Before
	public void setUp() throws Exception {
		XLS_File = "/xls/grouprename.xls";
		XML_File = "/dbunit/groupsdataset.xml";

		super.XLS_File = this.XLS_File;
		super.XML_File = this.XML_File;

		super.setUp();

		loadXLSFile(XLS_File);

	}

	@Test
	public void testShouldFindRenamedGroup() {

		DBskuGC.refreshData();

		// one group should appear
		assertEquals(1, DBskuGC.getUpdateDBGroupList().size());
		assertEquals("Любимов классический плитка", DBskuGC.getUpdateDBGroupList().get(0).getAfter());
		assertEquals("Любимов классический", DBskuGC.getUpdateDBGroupList().get(0).getBefore());

		assertEquals(0, DBskuGC.getAddDBGroupList().size());
	}

	@Test
	public void testShouldRenameNewGroup() {
		DBskuGC.refreshData();

		DBskuGC.getUpdateDBGroupList().get(0).setChecked(true);

		DBskuGC.applyChanges();

		/*
		 * try { new UnloadTestDatabase(); } catch (ClassNotFoundException |
		 * SQLException | IOException | DatabaseUnitException e) {
		 *
		 * e.printStackTrace(); }
		 */

		String expectedDatasetPath = "/dbunit/after_group_rename.xml";
		compareDatasets(expectedDatasetPath);

	}
}
