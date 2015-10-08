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

		return skuGropMap;
	}

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

		return dbRows;
	}

	public static void addSkuToDB(Changes c) throws SQLException, ClassNotFoundException, NamingException {
		Connection con = ConnectionManager.getDBConnection();
		PreparedStatement stmt = con.prepareStatement("INSERT INTO xx_rs_sku_hierarchy" + "           (parent_id"
				+ "           ,node_id" + "           ,is_group" + "           ,is_plan_group)" + "     VALUES"
				+ "           (?, ?, ?, ?)");

		stmt.setInt(1, Integer.parseInt(c.getAfter()));
		stmt.setInt(2, c.getId());
		stmt.setInt(3, 0);
		stmt.setInt(4, 0);
		stmt.executeUpdate();

	}
}
