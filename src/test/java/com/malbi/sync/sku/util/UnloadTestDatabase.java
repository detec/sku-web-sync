package com.malbi.sync.sku.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;

import com.malbi.sync.sku.db.DBUnitConfig;

public class UnloadTestDatabase {

	public UnloadTestDatabase()
			throws ClassNotFoundException, SQLException, FileNotFoundException, IOException, DatabaseUnitException {

		// will use properties not to rebuild old working code.
		Properties property = new Properties();
		property.setProperty("database.driver", DBUnitConfig.JDBC_DRIVER);
		property.setProperty("database.url", DBUnitConfig.JDBC_URL);
		property.setProperty("database.user", DBUnitConfig.USER);
		property.setProperty("database.password", DBUnitConfig.PASSWORD);

		System.out.println("Extracting dataset test.xml - start");

		Class driverClass = Class.forName(property.getProperty("database.driver"));
		H2Connection dbUnitCon = null;
		try (Connection jdbcConnection = DriverManager.getConnection(property.getProperty("database.url"),
				property.getProperty("database.user"), property.getProperty("database.password"));) {

			dbUnitCon = new H2Connection(jdbcConnection, property.getProperty("database.password"));
			DatabaseConfig dbUnitConConfig = dbUnitCon.getConfig();
			dbUnitConConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new Oracle10DataTypeFactory());
			dbUnitConConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

			// IDatabaseConnection connection = new
			// DatabaseConnection(dbUnitCon);

			// partial database export
			QueryDataSet partialDataSet = new QueryDataSet(dbUnitCon);
			partialDataSet.addTable("items", "select * from items");
			partialDataSet.addTable("xx_rs_sku_groups", "select * from xx_rs_sku_groups");
			partialDataSet.addTable("xx_rs_sku_hierarchy", "select * from xx_rs_sku_hierarchy");

			FlatXmlDataSet.write(partialDataSet, new FileOutputStream("test.xml"));
		} finally {
			if (dbUnitCon != null) {
				dbUnitCon.close();

			}
		}

		System.out.println("Extracting dataset test.xml to project root - end");

	}

}
