/**
 *
 */
package com.malbi.sync.sku.model;

/**
 * @author duplyk.a This class is intended for interactive changes of items.
 */
public class DialogueChanges extends Changes {

	public DialogueChanges(int key, String before, String after, DBSKUGroup parent) {
		super(key, before, after);
		this.parent = parent;
	}

	public DialogueChanges(Changes change) {
		super(change.key, change.before, change.after);
	}

	DBSKUGroup parent;

	public DBSKUGroup getParent() {
		return parent;
	}

	public void setParent(DBSKUGroup parent) {
		this.parent = parent;
	}

}
