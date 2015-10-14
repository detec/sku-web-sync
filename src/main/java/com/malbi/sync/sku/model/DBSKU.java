package com.malbi.sync.sku.model;

public class DBSKU extends AbstractDBEntity {

	public DBSKU() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DBSKU(int pKey) {
		key = pKey;
	}

	public DBSKU(int pKey, String pName) {
		key = pKey;
		name = pName;
	}

	public DBSKU(int pKey, String pName, int pParentId) {
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

		DBSKU other = (DBSKU) obj;
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
