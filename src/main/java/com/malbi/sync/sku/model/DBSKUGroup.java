package com.malbi.sync.sku.model;

/**
 * This is a class to represent DB SKU group in dialogue. It is almost the same
 * as SKU, that's why we use an=bstract class as superclass.
 *
 * @author Andrii Duplyk
 *
 */
public class DBSKUGroup extends AbstractDBEntity {

	public DBSKUGroup() {
		super();
	}

	public DBSKUGroup(int pKey) {
		key = pKey;
	}

	public DBSKUGroup(int pKey, String pName) {
		key = pKey;
		name = pName;
	}

	public DBSKUGroup(int pKey, String pName, int pParentId) {
		key = pKey;
		name = pName;
		parentId = pParentId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		return result;

	}

}
