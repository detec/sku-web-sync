package com.malbi.sync.sku.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import com.malbi.sync.sku.db.ConnectionManager;
import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKU;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DbRowData;

public class DAO {

	public static Map<Integer, String> getSkuMap() throws SQLException, ClassNotFoundException, NamingException {
		Map<Integer, String> skuMap = new HashMap<Integer, String>();
		ResultSet rs;

		Connection con = ConnectionManager.getDBConnection();
		Statement stmt = con.createStatement();

		rs = stmt.executeQuery(
				"select item_no" + " 	,item_desc" + "   from xx_bi_items_v" + "	where length(item_no)=9");
		while (rs.next()) {
			skuMap.put(rs.getInt(1), rs.getString(2));
		}

		con.close();

		return skuMap;
	}

	public static Map<Integer, String> getSkuGroupMap() throws SQLException, ClassNotFoundException, NamingException {
		Map<Integer, String> skuGropMap = new HashMap<Integer, String>();
		ResultSet rs;

		Connection con = ConnectionManager.getDBConnection();
		Statement stmt = con.createStatement();

		rs = stmt.executeQuery("select group_id" + "  ,group_name" + "  from xx_rs_sku_groups");
		while (rs.next()) {
			skuGropMap.put(rs.getInt(1), rs.getString(2));
		}

		con.close();
		return skuGropMap;
	}

	// public static List<DBSKUGroup> getDBSKUgroupsList() throws
	// ClassNotFoundException, SQLException, NamingException {

	// Map<Integer, String> skuGropMap = getSkuGroupMap();
	// List<DBSKUGroup> DBGroupList = new ArrayList<DBSKUGroup>();
	// skuGropMap.entrySet().stream()
	// .forEach(t -> DBGroupList.add(new DBSKUGroup(t.getKey().intValue(),
	// t.getValue())));
	// return DBGroupList;
	// }

	public static Map<Integer, DbRowData> getSkuHierarchyMap()
			throws SQLException, ClassNotFoundException, NamingException {

		Map<Integer, DbRowData> dbRows = new HashMap<>();
		ResultSet rs;

		Connection con = ConnectionManager.getDBConnection();
		Statement stmt = con.createStatement();

		rs = stmt.executeQuery("select parent_id" + "  ,node_id" + "  ,is_group" + "  ,is_plan_group"
				+ "  from xx_rs_sku_hierarchy" + "  where is_group=0");
		while (rs.next()) {
			dbRows.put(rs.getInt(2), new DbRowData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
		}

		con.close();

		return dbRows;
	}

	public static DBSKUGroup getDBSKUGroupById(int pId) throws ClassNotFoundException, SQLException, NamingException {
		DBSKUGroup skuGroup = new DBSKUGroup();

		Connection con = ConnectionManager.getDBConnection();
		ResultSet rs;
		PreparedStatement stmt = con.prepareStatement(
				"select group_id" + " ,group_name" + " from xx_rs_sku_groups" + " where group_id = ?");

		stmt.setInt(1, pId);
		// rs = stmt.executeQuery("select group_id"
		// + " ,group_name"
		// + " from xx_rs_sku_groups"
		// + "where group_id = ?");

		rs = stmt.executeQuery();
		while (rs.next()) {
			// skuGropMap.put(rs.getInt(1), rs.getString(2));
			skuGroup = new DBSKUGroup(rs.getInt(1), rs.getString(2));
		}

		con.close();
		return skuGroup;
	}

	// changed
	public static void addSkuToDB(DBSKU sku) throws SQLException, ClassNotFoundException, NamingException {
		Connection con = ConnectionManager.getDBConnection();
		PreparedStatement stmt = con.prepareStatement("INSERT INTO xx_rs_sku_hierarchy" + "           (parent_id"
				+ "           ,node_id" + "           ,is_group" + "           ,is_plan_group)" + "     VALUES"
				+ "           (?, ?, ?, ?)");

		stmt.setInt(1, sku.getParentId());
		stmt.setInt(2, sku.getId());
		stmt.setInt(3, 0);
		stmt.setInt(4, 0);
		stmt.executeUpdate();

		con.close();

	}

	public static void renameGroup(Changes changes) throws ClassNotFoundException, SQLException, NamingException {
		Connection con = ConnectionManager.getDBConnection();
		PreparedStatement stmt = con
				.prepareStatement("UPDATE xx_rs_sku_groups" + "   SET group_name = ?" + "   WHERE group_id = ?");
		stmt.setString(1, changes.getAfter());
		stmt.setInt(2, changes.getId());
		stmt.executeUpdate();

		con.close();
	}

	public static void addNewGroup(int parentId, Changes changes)
			throws ClassNotFoundException, SQLException, NamingException {
		PreparedStatement pStmt;
		Connection con = ConnectionManager.getDBConnection();

		pStmt = con.prepareStatement("INSERT INTO xx_rs_sku_groups" + "           (group_id" + "           ,group_name)"
				+ "     VALUES" + "           (?, ?)");
		pStmt.setInt(1, changes.getId());
		pStmt.setString(2, changes.getAfter());
		pStmt.executeUpdate();
		// System.out.println("Group added to list: ID " + changes.getId() +
		// ", Name " + changes.getAfter() + ".");

		// add to hierarchy
		pStmt = con.prepareStatement("INSERT INTO xx_rs_sku_hierarchy" + "           (parent_id" + "           ,node_id"
				+ "           ,is_group" + "           ,is_plan_group)" + "     VALUES" + "           (?, ?, ?, ?)");
		pStmt.setInt(1, parentId);
		pStmt.setInt(2, changes.getId());
		pStmt.setInt(3, 1);
		pStmt.setInt(4, 0);
		pStmt.executeUpdate();

		con.close();

	}

	// changed
	public static void moveSkuToAnotherGroup(DBSKU sku) throws SQLException, ClassNotFoundException, NamingException {
		PreparedStatement pStmt;
		Connection con = ConnectionManager.getDBConnection();

		pStmt = con.prepareStatement("UPDATE xx_rs_sku_hierarchy" + "   SET parent_id = ?" + " WHERE node_id = ?");
		// pStmt.setInt(1, Integer.parseInt(c.getAfter()));
		// pStmt.setInt(2, c.getId());
		pStmt.setInt(1, sku.getParentId());
		pStmt.setInt(2, sku.getId());
		pStmt.executeUpdate();

	}

	// changed, deletes SKU from hierarchy
	public static void deleteSku(DBSKU sku) throws ClassNotFoundException, SQLException, NamingException {
		PreparedStatement pStmt;
		Connection con = ConnectionManager.getDBConnection();

		pStmt = con.prepareStatement("DELETE FROM xx_rs_sku_hierarchy" + "      WHERE node_id = ?");
		pStmt.setInt(1, sku.getId());
		pStmt.executeUpdate();

	}

}
