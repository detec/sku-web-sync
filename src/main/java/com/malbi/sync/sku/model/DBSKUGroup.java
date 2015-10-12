package com.malbi.sync.sku.model;

// This is a class to represent DB SKU group in dialogue.
public class DBSKUGroup {

	public DBSKUGroup() {

	}

	// we need this id-constructor to convert from string to id in Option-menu.
	public DBSKUGroup(int key) {
		this.key = key;
	}

	public DBSKUGroup(int key, String name) {
		this.key = key;
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DBSKUGroup other = (DBSKUGroup) obj;
		if (key != other.key) {
			return false;
		}
		return true;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	int key;
	String name;
}
