package com.malbi.sync.sku.jsfcontrollers;

import org.junit.Before;
import org.junit.Test;

import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DialogueChanges;

public class DBSKUGroupsAddTests extends DBSKUGroupsAbstractTest {

	public DBSKUGroupsAddTests(String name) {
		super(name);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		XLS_File = "/xls/groupadd.xls";
		XML_File = "/dbunit/groupsdataset.xml";

		super.XLS_File = this.XLS_File;
		super.XML_File = this.XML_File;

		super.setUp();

		loadXLSFile(XLS_File);

	}

	@Test
	public void testShouldFindNewGroup() {

		DBskuGC.refreshData();

		// one group should appear
		assertEquals(1, DBskuGC.getAddDBGroupList().size());
		assertEquals(668, DBskuGC.getAddDBGroupList().get(0).getKey());

		assertEquals(0, DBskuGC.getUpdateDBGroupList().size());
	}

	@Test
	public void testShouldInsertNewGroup() {
		DBskuGC.refreshData();

		DialogueChanges group = DBskuGC.getAddDBGroupList().get(0);
		group.setChecked(true);

		DBSKUGroup parentGroup = new DBSKUGroup(1020, "Шоколад Любимов");
		parentGroup.setParentId(1018);
		group.setParent(parentGroup);

		DBskuGC.applyChanges();

		/*
		 * try { new UnloadTestDatabase(); } catch (ClassNotFoundException |
		 * SQLException | IOException | DatabaseUnitException e) {
		 *
		 * e.printStackTrace(); }
		 */
		// here we should check if group was really added to DB.
		// Fetch database data after executing your code

		String expectedDatasetPath = "/dbunit/after_new_group_insert.xml";
		compareDatasets(expectedDatasetPath);
	}

}
