package com.malbi.sync.sku.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named("SQLQueries")
@ApplicationScoped
public class SQLQueries implements Serializable {

	private static final long serialVersionUID = 6628491051063758349L;

	public Properties getProperty() {
		if (property.isEmpty()) {
			loadProperties();
		}
		return property;
	}

	public void setProperty(Properties property) {
		this.property = property;
	}

	private Properties property = new Properties();

	// http://stackoverflow.com/questions/8740234/postconstruct-checked-exceptions
	public void loadProperties() {

		// using try with resources
		try (InputStream in = getClass().getResourceAsStream("/SQLQueries.properties")) {
			property.load(in);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
