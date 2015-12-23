package com.malbi.sync.sku.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.BeforeClass;

// http://devcolibri.com/3575

public class DBUnitConfig extends DBTestCase {

	protected IDatabaseTester tester;
	protected IDataSet beforeData;

	private static final String JDBC_DRIVER = "org.h2.Driver";
	private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private static final String USER = "sa";
	private static final String PASSWORD = "";

	// we should create schema!!
	// http://www.marcphilipp.de/blog/2012/03/13/database-tests-with-dbunit-part-1/

	// http://stackoverflow.com/questions/1531324/is-there-any-way-for-dbunit-to-automatically-create-tables

	private static void createH2Tables(IDataSet dataSet, Connection connection) throws DataSetException, SQLException {
		String[] tableNames = dataSet.getTableNames();

		String sql = "";
		for (String tableName : tableNames) {
			ITable table = dataSet.getTable(tableName);
			ITableMetaData metadata = table.getTableMetaData();
			Column[] columns = metadata.getColumns();

			sql += "create table " + tableName + "( ";
			boolean first = true;
			for (Column column : columns) {
				if (!first) {
					sql += ", ";
				}
				String columnName = column.getColumnName();
				String type = resolveType((String) table.getValue(0, columnName));
				sql += columnName + " " + type;
				if (first) {
					sql += " primary key";
					first = false;
				}
			}
			sql += "); ";
		}
		PreparedStatement pp = connection.prepareStatement(sql);
		pp.executeUpdate();

		// 23.12.2015 added
		pp.close();
		connection.close();

	}

	private static String resolveType(String str) {
		try {
			if (new Double(str).toString().equals(str)) {
				return "double";
			}
			if (new Integer(str).toString().equals(str)) {
				return "int";
			}
		} catch (Exception e) {
		}

		return "varchar";
	}

	@BeforeClass // Junit 3 and DBUnit do not understand it
	public static void createSchema() throws Exception {

		InputStream in = DBUnitConfig.class.getResourceAsStream("/dbunit/xlstester.xml");
		FlatXmlDataSet DBData = new FlatXmlDataSetBuilder().build(in);

		MockConnectionManager cm = new MockConnectionManager();
		createH2Tables(DBData, cm.getDBConnection());

		// RunScript.execute(JDBC_URL, USER, PASSWORD, "schema.sql",
		// Charset.forName("UTF-8"), false);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		tester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);

		// this is an analogue to BeforeClass
		createSchema();
	}

	public DBUnitConfig(String name) {
		super(name);

		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, JDBC_DRIVER);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, JDBC_URL);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, USER);
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, PASSWORD);
	}

	@Override
	protected IDataSet getDataSet() throws Exception {

		return beforeData;
	}

	@Override
	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.DELETE_ALL;
	}

}
