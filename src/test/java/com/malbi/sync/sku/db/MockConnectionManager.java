package com.malbi.sync.sku.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Let's try http://www.javaranch.com/journal/2003/12/UnitTestingDatabaseCode.html

public class MockConnectionManager implements IConnectionManager {

	@Override
	public Connection getDBConnection() throws SQLException {
		Connection con = null;
		try {
			Class driverClass = Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return con;
	}

}
