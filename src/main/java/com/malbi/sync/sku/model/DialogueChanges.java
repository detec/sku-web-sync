/**
 *
 */
package com.malbi.sync.sku.model;

import javax.validation.constraints.NotNull;

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

	@NotNull(message = "Родительская группа не может быть пустой! Если это группа верхнего уровня - поставьте родительскую группу Итого!")
	DBSKUGroup parent;

	public DBSKUGroup getParent() {
		return parent;
	}

	public void setParent(DBSKUGroup parent) {
		this.parent = parent;
	}

}
