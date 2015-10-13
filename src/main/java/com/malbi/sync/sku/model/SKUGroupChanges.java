package com.malbi.sync.sku.model;

public class SKUGroupChanges {
	int key;

	public SKUGroupChanges(int key, DBSKUGroup pBefore, DBSKUGroup pAfter) {
		this.key = key;
		this.before = pBefore;
		this.after = pAfter;
	}

	public SKUGroupChanges() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((after == null) ? 0 : after.hashCode());
		result = prime * result + ((before == null) ? 0 : before.hashCode());
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
		SKUGroupChanges other = (SKUGroupChanges) obj;
		if (after == null) {
			if (other.after != null) {
				return false;
			}
		} else if (!after.equals(other.after)) {
			return false;
		}
		if (before == null) {
			if (other.before != null) {
				return false;
			}
		} else if (!before.equals(other.before)) {
			return false;
		}
		if (key != other.key) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SKUGroupChanges [key=" + key + "]";
	}

	DBSKUGroup before;
	DBSKUGroup after;

	// GUI field.
	boolean checked;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public DBSKUGroup getBefore() {
		return before;
	}

	public void setBefore(DBSKUGroup before) {
		this.before = before;
	}

	public DBSKUGroup getAfter() {
		return after;
	}

	public void setAfter(DBSKUGroup after) {
		this.after = after;
	}

	public int getId() {
		return this.key;
	}

	public void setId(int pId) {
		this.key = pId;
	}

}
