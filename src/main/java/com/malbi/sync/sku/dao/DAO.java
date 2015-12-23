package com.malbi.sync.sku.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;

import com.malbi.sync.sku.db.IConnectionManager;
import com.malbi.sync.sku.db.SQLQueries;
import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKU;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DbRowData;

@Named
@SessionScoped
public class DAO implements Serializable {

	// private Connection connection;
	//
	// // this method is used to simplify testing
	// public Connection getConnection() throws SQLException {
	// try (Connection conn = cm.getDBConnection();) {
	// this.connection = conn;
	// }
	//
	// return this.connection;
	//
	// }
	//
	// public void setConnection(Connection connection) {
	// this.connection = connection;
	// }

	public Connection getSQLConnection() throws SQLException {
		return cm.getDBConnection();
	}

	public Map<Integer, String> getSkuMap() throws SQLException, ClassNotFoundException, NamingException {
		Map<Integer, String> skuMap = new HashMap<Integer, String>();
		// ResultSet rs;
		String query = sqlQueries.getProperty().getProperty("sku.map.select");

		// try with resources
		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				Statement stmt = con.createStatement();

				ResultSet rs = stmt.executeQuery(
						// "select item_no" + " ,item_desc" + " from
						// xx_bi_items_v" + "
						// where length(item_no)=9"
						query

		);) {
			while (rs.next()) {
				skuMap.put(rs.getInt(1), rs.getString(2));
			}
		}

		// or we get ORA-01000: maximum open cursors exceeded
		// stmt.close();
		// rs.close();
		// con.close();

		return skuMap;
	}

	public Map<Integer, String> getSkuGroupMap() throws SQLException, ClassNotFoundException, NamingException {
		Map<Integer, String> skuGropMap = new HashMap<Integer, String>();
		// ResultSet rs;

		String query = sqlQueries.getProperty().getProperty("sku.group.map.select");

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				Statement stmt = con.createStatement();

				ResultSet rs = stmt.executeQuery(
						// "select group_id" + " ,group_name" + " from
						// xx_rs_sku_groups"

						query

		);) {
			while (rs.next()) {
				skuGropMap.put(rs.getInt(1), rs.getString(2));
			}
		}

		// stmt.close();
		// rs.close(); // or we get ORA-01000: maximum open cursors exceeded
		// con.close();
		return skuGropMap;
	}

	public Map<Integer, DbRowData> getSkuHierarchyMap() throws SQLException, ClassNotFoundException, NamingException {

		Map<Integer, DbRowData> dbRows = new HashMap<>();
		// ResultSet rs;

		String query = sqlQueries.getProperty().getProperty("sku.hierarchy.map.select");

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				Statement stmt = con.createStatement();

				ResultSet rs = stmt.executeQuery(
						// "select parent_id" + " ,node_id" + " ,is_group" + "
						// ,is_plan_group"
						// + " from xx_rs_sku_hierarchy" + " where is_group=0"

						query

		);) {
			while (rs.next()) {
				dbRows.put(rs.getInt(2), new DbRowData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
			}
		}
		// stmt.close();
		// rs.close(); // or we get ORA-01000: maximum open cursors exceeded
		// con.close();

		return dbRows;
	}

	public DBSKUGroup getDBSKUGroupById(int pId) throws ClassNotFoundException, SQLException, NamingException {
		DBSKUGroup skuGroup = new DBSKUGroup();
		String query = sqlQueries.getProperty().getProperty("dbsku.group.by.id.select");
		// ResultSet rs;

		// http://stackoverflow.com/questions/8066501/how-should-i-use-try-with-resources-with-jdbc
		// setting parameters example

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				PreparedStatement stmt = con.prepareStatement(
						// "select group_id" + " ,group_name" + " from
						// xx_rs_sku_groups"
						// + " where group_id = ?"

						query);) {

			stmt.setInt(1, pId);

			try (ResultSet rs = stmt.executeQuery();) {
				while (rs.next()) {
					// skuGropMap.put(rs.getInt(1), rs.getString(2));
					skuGroup = new DBSKUGroup(rs.getInt(1), rs.getString(2));
				}
			}
		}
		// stmt.close();
		// rs.close(); // or we get ORA-01000: maximum open cursors
		// // exceeded
		// con.close();
		return skuGroup;

	}

	// changed
	public void addSkuToDB(DBSKU sku) throws SQLException, ClassNotFoundException, NamingException {

		String query = sqlQueries.getProperty().getProperty("add.sku.to.db.insert");

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				PreparedStatement stmt = con.prepareStatement(
						// "INSERT INTO xx_rs_sku_hierarchy" + " (parent_id"
						// + " ,node_id" + " ,is_group" + " ,is_plan_group)" + "
						// VALUES"
						// + " (?, ?, ?, ?)"

						query

		);) {
			stmt.setInt(1, sku.getParentId());
			stmt.setInt(2, sku.getId());
			stmt.setInt(3, 0);
			stmt.setInt(4, 0);
			stmt.executeUpdate();
		}
		// or we get ORA-01000: maximum open cursors exceeded
		// stmt.close();
		// con.close();

	}

	public void renameGroup(Changes changes) throws ClassNotFoundException, SQLException, NamingException {
		String query = sqlQueries.getProperty().getProperty("rename.group.update");

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				PreparedStatement stmt = con.prepareStatement(
						// "UPDATE xx_rs_sku_groups" + " SET group_name = ?" + "
						// WHERE
						// group_id = ?"

						query

		);) {
			stmt.setString(1, changes.getAfter());
			stmt.setInt(2, changes.getId());
			stmt.executeUpdate();
		}
		// or we get ORA-01000: maximum open cursors exceeded
		// stmt.close();
		// con.close();
	}

	public void updateGroup(DBSKUGroup group) throws ClassNotFoundException, SQLException, NamingException {
		String query = sqlQueries.getProperty().getProperty("rename.group.update");

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				PreparedStatement stmt = con.prepareStatement(
						// "UPDATE xx_rs_sku_groups" + " SET group_name = ?" + "
						// WHERE
						// group_id = ?"

						query

		);) {
			stmt.setString(1, group.getName());
			stmt.setInt(2, group.getId());
			stmt.executeUpdate();
		}
		// or we get ORA-01000: maximum open cursors exceeded
		// stmt.close();
		// con.close();
	}

	public void addNewGroup(int parentId, Changes changes)
			throws ClassNotFoundException, SQLException, NamingException {

		String queryIntoGroups = sqlQueries.getProperty().getProperty("add.new.group.insert");
		String queryIntoHierarchy = sqlQueries.getProperty().getProperty("add.new.group.sku.hierarchy.insert");

		// PreparedStatement pStmt;
		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				PreparedStatement pStmt = con.prepareStatement(
						// "INSERT INTO xx_rs_sku_groups" + " (group_id" + "
						// ,group_name)"
						// + " VALUES" + " (?, ?)"

						queryIntoGroups

		);) {
			pStmt.setInt(1, changes.getId());
			pStmt.setString(2, changes.getAfter());

			pStmt.executeUpdate();

			// System.out.println("Group added to list: ID " + changes.getId() +
			// ", Name " + changes.getAfter() + ".");

			// add to hierarchy
			try (PreparedStatement pStmtHierarchy = con.prepareStatement(
					// "INSERT INTO xx_rs_sku_hierarchy"
					// + " (parent_id" + " ,node_id" + " ,is_group"
					// + " ,is_plan_group)" + " VALUES" + " (?, ?, ?, ?)"

					queryIntoHierarchy

			);) {
				pStmt.setInt(1, parentId);
				pStmt.setInt(2, changes.getId());
				pStmt.setInt(3, 1);
				pStmt.setInt(4, 0);
				pStmt.executeUpdate();

			}
		}

		// or we get ORA-01000: maximum open cursors exceeded
		// pStmt.close();
		// con.close();

	}

	// changed
	public void moveSkuToAnotherGroup(DBSKU sku) throws SQLException, ClassNotFoundException, NamingException {
		// PreparedStatement pStmt;

		String query = sqlQueries.getProperty().getProperty("move.sku.to.another.group.update");

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				PreparedStatement pStmt = con.prepareStatement(
						// "UPDATE xx_rs_sku_hierarchy" + " SET parent_id = ?" +
						// " WHERE
						// node_id
						// = ?"
						query

		);) {
			// pStmt.setInt(1, Integer.parseInt(c.getAfter()));
			// pStmt.setInt(2, c.getId());
			pStmt.setInt(1, sku.getParentId());
			pStmt.setInt(2, sku.getId());

			pStmt.executeUpdate();

		}
		// or we get ORA-01000: maximum open cursors exceeded
		// pStmt.close();
		// con.close();

	}

	// changed, deletes SKU from hierarchy
	public void deleteSku(DBSKU sku) throws ClassNotFoundException, SQLException, NamingException {
		// PreparedStatement pStmt;
		String query = sqlQueries.getProperty().getProperty("delete.sku.from.hierarchy.delete");

		try (

				// Connection con = cm.getDBConnection();
				Connection con = getSQLConnection();

				PreparedStatement pStmt = con.prepareStatement(
						// "DELETE FROM xx_rs_sku_hierarchy" + " WHERE node_id =
						// ?"
						query

		);) {
			pStmt.setInt(1, sku.getId());
			pStmt.executeUpdate();
		}
		// or we get ORA-01000: maximum open cursors exceeded
		// pStmt.close();
		// con.close();
	}

	private static final long serialVersionUID = 3487388043292573257L;

	@Inject
	private IConnectionManager cm;

	@Inject
	private SQLQueries sqlQueries;

	public SQLQueries getSqlQueries() {
		return sqlQueries;
	}

	public void setSqlQueries(SQLQueries sqlQueries) {
		this.sqlQueries = sqlQueries;
	}

	public IConnectionManager getCm() {
		return cm;
	}

	public void setCm(IConnectionManager cm) {
		this.cm = cm;
	}

}
