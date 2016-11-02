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

import com.malbi.sync.sku.db.IConnectionManager;
import com.malbi.sync.sku.db.SQLQueries;
import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKU;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DbRowData;

@Named
@SessionScoped
public class DAO implements Serializable {

	private static final long serialVersionUID = 3487388043292573257L;

	@Inject
	private IConnectionManager cm;

	@Inject
	private SQLQueries sqlQueries;

	public Connection getSQLConnection() throws SQLException {
		return cm.getDBConnection();
	}

	public Map<Integer, String> getSkuMap() throws Exception {
		Map<Integer, String> skuMap = new HashMap<>();
		String query = sqlQueries.getProperty().getProperty("sku.map.select");

		// try with resources
		try (Connection con = getSQLConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);) {
			while (rs.next()) {
				skuMap.put(rs.getInt(1), rs.getString(2));
			}
		}

		return skuMap;
	}

	public Map<Integer, String> getSkuGroupMap() throws Exception {
		Map<Integer, String> skuGropMap = new HashMap<>();

		String query = sqlQueries.getProperty().getProperty("sku.group.map.select");

		try (Connection con = getSQLConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);) {
			while (rs.next()) {
				skuGropMap.put(rs.getInt(1), rs.getString(2));
			}
		}

		return skuGropMap;
	}

	public Map<Integer, DbRowData> getSkuHierarchyMap() throws Exception {

		Map<Integer, DbRowData> dbRows = new HashMap<>();
		String query = sqlQueries.getProperty().getProperty("sku.hierarchy.map.select");

		try (Connection con = getSQLConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);) {
			while (rs.next()) {
				dbRows.put(rs.getInt(2), new DbRowData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
			}
		}

		return dbRows;
	}

	public DBSKUGroup getDBSKUGroupById(int pId) throws Exception {
		DBSKUGroup skuGroup = new DBSKUGroup();
		String query = sqlQueries.getProperty().getProperty("dbsku.group.by.id.select");
		// http://stackoverflow.com/questions/8066501/how-should-i-use-try-with-resources-with-jdbc
		// setting parameters example

		try (Connection con = getSQLConnection(); PreparedStatement stmt = con.prepareStatement(query);) {
			stmt.setInt(1, pId);
			try (ResultSet rs = stmt.executeQuery();) {
				while (rs.next()) {
					skuGroup = new DBSKUGroup(rs.getInt(1), rs.getString(2));
				}
			}
		}

		return skuGroup;

	}

	// changed
	public void addSkuToDBHierarchy(DBSKU sku) throws Exception {

		String query = sqlQueries.getProperty().getProperty("add.sku.to.db.insert");

		try (Connection con = getSQLConnection(); PreparedStatement stmt = con.prepareStatement(query);) {
			stmt.setInt(1, sku.getParentId());
			stmt.setInt(2, sku.getId());
			stmt.setInt(3, 0);
			stmt.setInt(4, 0);
			stmt.executeUpdate();
		}

	}

	public void renameGroup(Changes changes) throws Exception {
		String query = sqlQueries.getProperty().getProperty("rename.group.update");

		try (Connection con = getSQLConnection(); PreparedStatement stmt = con.prepareStatement(query);) {
			stmt.setString(1, changes.getAfter());
			stmt.setInt(2, changes.getId());
			stmt.executeUpdate();
		}

	}

	public void updateGroup(DBSKUGroup group) throws Exception {
		String query = sqlQueries.getProperty().getProperty("rename.group.update");

		try (Connection con = getSQLConnection(); PreparedStatement stmt = con.prepareStatement(query);) {
			stmt.setString(1, group.getName());
			stmt.setInt(2, group.getId());
			stmt.executeUpdate();
		}

	}

	public void addNewGroup(int parentId, Changes changes) throws Exception {

		String queryIntoGroups = sqlQueries.getProperty().getProperty("add.new.group.insert");
		String queryIntoHierarchy = sqlQueries.getProperty().getProperty("add.new.group.sku.hierarchy.insert");

		try (Connection con = getSQLConnection(); PreparedStatement pStmt = con.prepareStatement(queryIntoGroups);) {
			pStmt.setInt(1, changes.getId());
			pStmt.setString(2, changes.getAfter());

			pStmt.executeUpdate();

			// add to hierarchy
			try (PreparedStatement pStmtHierarchy = con.prepareStatement(queryIntoHierarchy);) {
				pStmtHierarchy.setInt(1, parentId);
				pStmtHierarchy.setInt(2, changes.getId());
				pStmtHierarchy.setInt(3, 1);
				pStmtHierarchy.setInt(4, 0);
				pStmtHierarchy.executeUpdate();

			}
		}

	}

	// changed
	public void moveSkuToAnotherGroup(DBSKU sku) throws Exception {

		String query = sqlQueries.getProperty().getProperty("move.sku.to.another.group.update");

		try (Connection con = getSQLConnection(); PreparedStatement pStmt = con.prepareStatement(query);) {
			pStmt.setInt(1, sku.getParentId());
			pStmt.setInt(2, sku.getId());

			pStmt.executeUpdate();
		}

	}

	// changed, deletes SKU from hierarchy
	public void deleteSku(DBSKU sku) throws Exception {
		String query = sqlQueries.getProperty().getProperty("delete.sku.from.hierarchy.delete");

		try (Connection con = getSQLConnection(); PreparedStatement pStmt = con.prepareStatement(query);) {
			pStmt.setInt(1, sku.getId());
			pStmt.executeUpdate();
		}

	}

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
