package com.malbi.sync.sku.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.malbi.sync.sku.converter.Exception2String;
import com.malbi.sync.sku.dao.DAO;
import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKU;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DbRowData;
import com.malbi.sync.sku.model.SKUGroupChanges;

@Named
@SessionScoped
public class SKUService implements Serializable {

	private static final long serialVersionUID = 3013221122140047848L;

	@Inject
	private DAO dao;

	private String errorLog = "";

	public DAO getDAO() {
		return dao;
	}

	public void setDAO(DAO dAO) {
		dao = dAO;
	}

	public Map<Integer, String> getSkuMap() {
		Map<Integer, String> skuMap = new HashMap<>();

		try {
			skuMap = dao.getSkuMap();
		} catch (Exception e) {
			this.errorLog = "Не удалось получить список SKU из БД: \n" + Exception2String.printStackTrace(e);
		}

		return skuMap;
	}

	public Map<Integer, String> getSkuGroupMap() {
		Map<Integer, String> skuGropMap = new HashMap<>();
		try {
			skuGropMap = dao.getSkuGroupMap();
		} catch (Exception e) {
			this.errorLog = "Не удалось получить список групп SKU из БД: \n" + Exception2String.printStackTrace(e);
		}

		return skuGropMap;
	}

	public Map<Integer, DbRowData> getSkuHierarchyMap() {
		Map<Integer, DbRowData> dbRows = new HashMap<>();

		try {
			dbRows = dao.getSkuHierarchyMap();
		} catch (Exception e) {
			this.errorLog = "Не удалось получить иерархию SKU из БД: \n" + Exception2String.printStackTrace(e);
		}
		return dbRows;
	}

	// changed
	public boolean addSkuToDBHierarchy(SKUGroupChanges c) {
		boolean result = false;

		// In DAO we should send plain objects, not business-layer ones.
		DBSKU sku = new DBSKU();
		sku.setId(c.getId()); // id of the SKU
		sku.setParentId(c.getAfter().getId()); // id of the parent group.

		try {
			dao.addSkuToDBHierarchy(sku);
			result = true;
		} catch (Exception e) {
			this.errorLog = "Не удалось получить иерархию SKU из БД: \n" + Exception2String.printStackTrace(e);
			result = false;
		}
		return result;
	}

	public boolean renameGroup(Changes changes) {
		boolean result = false;

		DBSKUGroup group = new DBSKUGroup();
		group.setId(changes.getId());
		group.setName(changes.getAfter());

		try {
			dao.updateGroup(group);
			result = true;
		} catch (Exception e) {
			this.errorLog = "Не удалось переименовать группу SKU в БД: \n" + Exception2String.printStackTrace(e);
			result = false;
		}
		return result;
	}

	public boolean addNewGroup(int parentId, Changes changes) {
		boolean result = false;
		try {
			dao.addNewGroup(parentId, changes);
			result = true;
		} catch (Exception e) {
			this.errorLog = "Не удалось добавить группу SKU в БД: \n" + Exception2String.printStackTrace(e);
			result = false;
		}

		return result;
	}

	// Andrei Duplik added. Returns SKU group with id and name
	public List<DBSKUGroup> getDBSKUgroups() {
		List<DBSKUGroup> dbGroupList = new ArrayList<>();

		Map<Integer, String> skuGropMap = new HashMap<>();
		try {
			skuGropMap = dao.getSkuGroupMap();
		} catch (Exception e) {
			this.errorLog = "Не удалось получить список групп SKU из БД: \n" + Exception2String.printStackTrace(e);
		}

		skuGropMap.entrySet().stream()
				.forEach(t -> dbGroupList.add(new DBSKUGroup(t.getKey().intValue(), t.getValue())));
		return dbGroupList;
	}

	// changed
	public boolean moveSkuToAnotherGroup(SKUGroupChanges c) {
		boolean result = false;

		// In DAO we should send plain objects, not business-layer ones.
		DBSKU sku = new DBSKU();
		sku.setParentId(c.getAfter().getId());
		sku.setId(c.getId());

		try {
			dao.moveSkuToAnotherGroup(sku);
			result = true;
		} catch (Exception e) {
			this.errorLog = "Не удалось переместить SKU в другую группу: \n" + Exception2String.printStackTrace(e);
		}
		return result;
	}

	// changed
	public boolean deleteSku(SKUGroupChanges c) {
		boolean result = false;
		DBSKU sku = new DBSKU(c.getId());
		try {
			dao.deleteSku(sku);
			result = true;
		} catch (Exception e) {
			this.errorLog = "Не удалось удалить SKU: \n" + Exception2String.printStackTrace(e);
		}

		return result;
	}

	public DBSKUGroup getDBSKUGroupById(int pId) {
		try {
			return dao.getDBSKUGroupById(pId);
		} catch (Exception e) {
			this.errorLog = "Не удалось запросить в БД группу SKU: \n" + Exception2String.printStackTrace(e);
		}
		return new DBSKUGroup();
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

}
