package com.malbi.sync.sku.model;

public class DBSKU extends AbstractDBEntity {

	public DBSKU() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DBSKU(int pKey) {
		super.key = pKey;
	}

	public DBSKU(int pKey, String pName) {
		super.key = pKey;
		super.name = pName;
	}

	public DBSKU(int pKey, String pName, int pParentId) {
		super.key = pKey;
		super.name = pName;
		super.parentId = pParentId;
	}

}
