package com.malbi.sync.sku.model;

// This is a class to represent DB SKU group in dialogue.
// It is almost the same as SKU, that's why we use an=bstract class as superclass.
public class DBSKUGroup extends AbstractDBEntity {

	public DBSKUGroup() {
		super();
	}

	public DBSKUGroup(int pKey) {
		super.key = pKey;
	}

	public DBSKUGroup(int pKey, String pName) {
		super.key = pKey;
		super.name = pName;
	}

	public DBSKUGroup(int pKey, String pName, int pParentId) {
		super.key = pKey;
		super.name = pName;
		super.parentId = pParentId;
	}

}
