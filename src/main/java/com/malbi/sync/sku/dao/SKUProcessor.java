package com.malbi.sync.sku.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.service.SKUService;

public class SKUProcessor {

	public void processSkuGroupChanges(List<Changes> changes) {
		List<Changes> updateList = new ArrayList<>();
		List<Changes> addList = new ArrayList<>();
		for (Changes c : changes) {
			if (c.getBefore() == null) {
				addList.add(c);
			} else {
				updateList.add(c);
			}
		}

		String answer;
		// update groups
		if (updateList.size() > 0) {
			System.out.println("\nThise (" + updateList.size() + ") groups were renamed in source:");
			for (Changes c : updateList) {
				System.out.println(c);
				System.out.print("Rename group? >>> ");
				answer = MainApp.SCANNER.nextLine();
				if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
					try {
						renameGroup(c);
					} catch (ClassNotFoundException | SQLException e) {
						System.out.println("Wasn't able to update sku group name." + MainApp.END_MSG);
						e.printStackTrace();
						System.exit(1);
					}
					System.out.println("Group was renamed to: " + c.getAfter());
				} else {
					System.out.println("Group will remain unchanged.");
				}
			}
		}

		// add groups
		if (addList.size() > 0) {
			System.out.println("\nThise (" + addList.size() + ") groups were added to source:");
			boolean gropWasAdded = false;
			for (Changes c : addList) {
				System.out.println(c);
				do {
					System.out.print("Name the parent group (name or id): >>> ");
					answer = MainApp.SCANNER.nextLine();
					try {
						gropWasAdded = addNewGroup(answer, c);
					} catch (ClassNotFoundException | SQLException e) {
						System.out.println("Wasn't able to add sku group." + MainApp.END_MSG);
						e.printStackTrace();
						System.exit(1);
					}
					if (!gropWasAdded) {
						System.out.println("There is no such parent group.");
					}
				} while (!gropWasAdded);
			}
		}

		System.out.println("\nUpdate group procedure is finished.");
	}

	public void processSkuChanges(List<Changes> skuChanges) {
		// Get sku list from DB
		Map<Integer, String> skuMap = new HashMap<>();
		Map<Integer, String> skuGropMap = new HashMap<>();
		ResultSet rs;

		SKUService service = new SKUService();
		skuMap = service.getSkuMap();
		skuGropMap = service.getSkuGroupMap();

		// Create lists of changes
		List<Changes> updateList = new ArrayList<>();
		List<Changes> addList = new ArrayList<>();
		List<Changes> removeList = new ArrayList<>();
		for (Changes c : skuChanges)

		{
			if (c.getBefore() == null) {
				addList.add(c);
			} else if (c.getAfter() == null) {
				removeList.add(c);
			} else {
				updateList.add(c);
			}
		}

		String answer;
		// move sku
		if (updateList.size() > 0)

		{
			System.out.println("\nThise (" + updateList.size() + ") sku were moved to another group:");
			for (Changes c : updateList) {
				System.out.println("ID: " + c.getId() + ", Name: " + skuMap.get(c.getId()) + ", Group from: "
						+ skuGropMap.get(Integer.parseInt(c.getBefore())) + ", Group to: "
						+ skuGropMap.get(Integer.parseInt(c.getAfter())));
				System.out.print("Move to new group? >>> ");
				answer = MainApp.SCANNER.nextLine();
				if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
					try {
						moveSkuToAnotherGroup(c);
					} catch (ClassNotFoundException | SQLException e) {
						System.out.println("Wasn't able to move sku to another group." + MainApp.END_MSG);
						e.printStackTrace();
						System.exit(1);
					}
					System.out.println("Sku was moved to: " + skuGropMap.get(Integer.parseInt(c.getAfter())));
				} else {
					System.out.println("Sku will remain unchanged.");
				}
			}
		}

		// add sku
		if (addList.size() > 0)

		{
			System.out.println("\nThise (" + addList.size() + ") sku were added to source:");
			for (Changes c : addList) {
				System.out.println("ID: " + c.getId() + ", Name: " + skuMap.get(c.getId()) + ", Group: "
						+ skuGropMap.get(Integer.parseInt(c.getAfter())));
				System.out.print("Add to DB?: >>> ");
				answer = MainApp.SCANNER.nextLine();
				if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
					try {
						addSkuToDb(c);
					} catch (ClassNotFoundException | SQLException e) {
						System.out.println("Wasn't able to add new sku." + MainApp.END_MSG);
						e.printStackTrace();
						System.exit(1);
					}
					System.out.println("Sku was added to: " + skuGropMap.get(Integer.parseInt(c.getAfter())));
				} else {
					System.out.println("Sku will remain unchanged.");
				}

			}
		}

		// delete sku
		if (removeList.size() > 0)

		{
			System.out.println("\nThise (" + removeList.size() + ") sku were removed from source:");
			for (Changes c : removeList) {
				System.out.println("ID: " + c.getId() + ", Group: " + skuGropMap.get(Integer.parseInt(c.getBefore())));
				System.out.print("Delete from DB?: >>> ");
				answer = MainApp.SCANNER.nextLine();
				if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
					try {
						deleteSku(c);
					} catch (ClassNotFoundException | SQLException e) {
						System.out.println("Wasn't able to delete sku." + MainApp.END_MSG);
						e.printStackTrace();
						System.exit(1);
					}
					System.out.println("Sku was removed from DB");
				} else {
					System.out.println("Sku will remain unchanged.");
				}

			}
		}

		System.out.println("\nUpdate sku procedure is finished.");

	}

	private void deleteSku(Changes c) throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		try (Connection con = DriverManager.getConnection(connection, "xx", "xx");
				PreparedStatement stmt = con
						.prepareStatement("DELETE FROM xx_rs_sku_hierarchy" + "      WHERE node_id = ?");) {
			stmt.setInt(1, c.getId());
			stmt.executeUpdate();
		}
	}

	private void moveSkuToAnotherGroup(Changes c) throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		try (Connection con = DriverManager.getConnection(connection, "xx", "xx");
				PreparedStatement stmt = con.prepareStatement(
						"UPDATE xx_rs_sku_hierarchy" + "   SET parent_id = ?" + " WHERE node_id = ?");) {
			stmt.setInt(1, Integer.parseInt(c.getAfter()));
			stmt.setInt(2, c.getId());
			stmt.executeUpdate();
		}
	}

	private void renameGroup(Changes changes) throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		try (Connection con = DriverManager.getConnection(connection, "xx", "xx");
				PreparedStatement stmt = con.prepareStatement(
						"UPDATE xx_rs_sku_groups" + "   SET group_name = ?" + "   WHERE group_id = ?");) {
			stmt.setString(1, changes.getAfter());
			stmt.setInt(2, changes.getId());
			stmt.executeUpdate();
		}
	}

	private boolean addNewGroup(String parent, Changes changes) throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		ResultSet rs;
		PreparedStatement pStmt;
		/*
		 * If parent not exists return "There is no parent with such $" Else add
		 * new group to groups list and to hierarchy.
		 */
		try (Connection con = DriverManager.getConnection(connection, "xx", "xx")) {
			if (isInteger(parent)) {

				pStmt = con.prepareStatement("SELECT group_id" + "  FROM xx_rs_sku_groups" + "  WHERE group_id = ?");
				pStmt.setInt(1, Integer.parseInt(parent));
			} else {
				pStmt = con.prepareStatement("SELECT group_id" + "  FROM xx_rs_sku_groups" + "  WHERE group_name = ?");
				pStmt.setString(1, parent);
			}

			rs = pStmt.executeQuery();
			int parentId;
			// check if result set has data. If true add group to group list and
			// hierarchy.
			if (rs.next()) {
				parentId = rs.getInt(1);
				// add to group list
				pStmt = con.prepareStatement("INSERT INTO xx_rs_sku_groups" + "           (group_id"
						+ "           ,group_name)" + "     VALUES" + "           (?, ?)");
				pStmt.setInt(1, changes.getId());
				pStmt.setString(2, changes.getAfter());
				pStmt.executeUpdate();
				System.out.println("Group added to list: ID " + changes.getId() + ", Name " + changes.getAfter() + ".");

				// add to hierarchy
				pStmt = con.prepareStatement("INSERT INTO xx_rs_sku_hierarchy" + "           (parent_id"
						+ "           ,node_id" + "           ,is_group" + "           ,is_plan_group)" + "     VALUES"
						+ "           (?, ?, ?, ?)");
				pStmt.setInt(1, parentId);
				pStmt.setInt(2, changes.getId());
				pStmt.setInt(3, 1);
				pStmt.setInt(4, 0);
				pStmt.executeUpdate();
				System.out.println("Group was added to hierarchy: parentID " + parentId + ", nodeID " + changes.getId()
						+ ", isGroup 1, isPlanGroup 0.");
			} else {
				return false;
			}
		}

		return true;
	}

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}
}
