package com.malbi.sync.sku.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConnectionManager {

	public static Connection getDBConnection() throws SQLException, ClassNotFoundException, NamingException {

		com.sun.appserv.jdbc.DataSource ds = null;
		InitialContext initialContext = new InitialContext();

		Context dbContext = (Context) initialContext.lookup("java:comp/env");
		ds = (com.sun.appserv.jdbc.DataSource) dbContext.lookup("Oracle");
		Connection con = null;

		// java:app/jdbc/
		if (ds != null) {
			con = ds.getConnection();

			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)) {
				con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			}
		} else {
			throw new SQLException("Datasource from Glassfish application server returned as null!");
		}
		return con;
	}
}
