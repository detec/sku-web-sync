package com.malbi.sync.sku.db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named("ConnectionManager")
@ApplicationScoped
public class ConnectionManager implements Serializable, IConnectionManager {

	private static final long serialVersionUID = -393036417948357440L;

	@Resource(name = "Oracle")
	private com.sun.appserv.jdbc.DataSource dataSource;

	@Override
	public Connection getDBConnection() throws SQLException {
		Connection con;
		if (dataSource != null) {
			con = dataSource.getConnection();

			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)) {
				con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			}
		} else {
			throw new SQLException("Datasource from GlassFish returned as null!");
		}
		return con;
	}

}
