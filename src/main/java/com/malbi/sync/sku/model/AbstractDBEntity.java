package com.malbi.sync.sku.model;

public abstract class AbstractDBEntity {

	int key = 0;
	String name = "";
	int parentId = 0;

	public int getId() {
		return key;
	}

	public void setId(int key) {
		this.key = key;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + parentId;
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
		AbstractDBEntity other = (AbstractDBEntity) obj;
		if (key != other.key) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (parentId != other.parentId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[name=" + name + "]";
	}

}
