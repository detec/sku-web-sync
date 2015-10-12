package com.malbi.sync.sku.model;

public class Changes {

	int key;
	String before = "";
	String after = "";

	// GUI field.
	boolean checked;

	public Changes(int key, String before, String after) {
		this.key = key;
		this.before = before;
		this.after = after;
	}

	public int getId() {
		return key;
	}

	public int getKey() {
		return this.key;
	}

	public void setKey(int pkey) {
		this.key = pkey;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getBefore() {
		return before;
	}

	public String getAfter() {
		return after;
	}

	@Override
	public String toString() {
		return "KEY: " + key + ", BEFORE: " + before + ", AFTER: " + after;
	}
}