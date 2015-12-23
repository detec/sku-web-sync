package com.malbi.sync.sku.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionManager {
	public Connection getDBConnection() throws SQLException;

}
