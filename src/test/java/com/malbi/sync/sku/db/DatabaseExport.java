package com.malbi.sync.sku.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.ext.oracle.OracleConnection;

public class DatabaseExport {

	// http://dbunit.sourceforge.net/faq.html#extract
	public static void main(String[] args) throws Exception {
		System.out.println("Extracting dataset work.xml to project root - start");

		Properties property = new Properties();
		// ClassLoader cl = Thread.currentThread().getContextClassLoader();

		try (InputStream in = DatabaseExport.class.getResourceAsStream("/dbunit/work_connection.properties")) {
			property.load(in);
		} catch (IOException e) {

			e.printStackTrace();
		}

		// org.dbunit.ext.oracle.Oracle10DataTypeFactory
		Class driverClass = Class.forName(property.getProperty("database.driver"));
		OracleConnection dbUnitCon = null;
		try (Connection jdbcConnection = DriverManager.getConnection(property.getProperty("database.url"),
				property.getProperty("database.user"), property.getProperty("database.password"));) {

			dbUnitCon = new OracleConnection(jdbcConnection, property.getProperty("database.password"));
			DatabaseConfig dbUnitConConfig = dbUnitCon.getConfig();
			dbUnitConConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new Oracle10DataTypeFactory());
			dbUnitConConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

			// IDatabaseConnection connection = new
			// DatabaseConnection(dbUnitCon);

			// partial database export
			QueryDataSet partialDataSet = new QueryDataSet(dbUnitCon);
			partialDataSet.addTable("items", "select item_no ,item_desc from xx_bi_items_v where length(item_no)=9");
			partialDataSet.addTable("xx_rs_sku_groups", "select group_id, group_name from xx_rs_sku_groups");
			partialDataSet.addTable("xx_rs_sku_hierarchy",
					"select parent_id, node_id, is_group, is_plan_group from xx_rs_sku_hierarchy");

			FlatXmlDataSet.write(partialDataSet, new FileOutputStream("work.xml"));
		} finally {
			if (dbUnitCon != null) {
				dbUnitCon.close();

			}
		}

		System.out.println("Extracting dataset work.xml to project root - end");

	}
}
