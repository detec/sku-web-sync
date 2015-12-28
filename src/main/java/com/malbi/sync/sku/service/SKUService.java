package com.malbi.sync.sku.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;

import com.malbi.sync.sku.dao.DAO;
import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKU;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DbRowData;
import com.malbi.sync.sku.model.SKUGroupChanges;

@Named
@SessionScoped
public class SKUService implements Serializable {

	public DAO getDAO() {
		return DAO;
	}

	public void setDAO(DAO dAO) {
		DAO = dAO;
	}

	public Map<Integer, String> getSkuMap() {
		Map<Integer, String> skuMap = new HashMap<Integer, String>();

		try {
			skuMap = DAO.getSkuMap();
		} catch (SQLException | ClassNotFoundException | NamingException e) {
			this.ErrorLog = "Не удалось получить список SKU из БД: \n" + e.getMessage();
		}

		return skuMap;
	}

	public Map<Integer, String> getSkuGroupMap() {
		Map<Integer, String> skuGropMap = new HashMap<Integer, String>();
		try {
			skuGropMap = DAO.getSkuGroupMap();
		} catch (SQLException | ClassNotFoundException | NamingException e) {
			this.ErrorLog = "Не удалось получить список групп SKU из БД: \n" + e.getMessage();
		}

		return skuGropMap;
	}

	public Map<Integer, DbRowData> getSkuHierarchyMap() {
		Map<Integer, DbRowData> dbRows = new HashMap<>();

		try {
			dbRows = DAO.getSkuHierarchyMap();
		} catch (SQLException | ClassNotFoundException | NamingException e) {
			this.ErrorLog = "Не удалось получить иерархию SKU из БД: \n" + e.getMessage();
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
			DAO.addSkuToDBHierarchy(sku);
			result = true;
		} catch (SQLException | ClassNotFoundException | NamingException e) {
			this.ErrorLog = "Не удалось получить иерархию SKU из БД: \n" + e.getMessage();
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
			// DAO.renameGroup(changes);
			DAO.updateGroup(group);
			result = true;
		} catch (ClassNotFoundException | SQLException | NamingException e) {
			this.ErrorLog = "Не удалось переименовать группу SKU в БД: \n" + e.getMessage();
			result = false;
		}
		return result;
	}

	public boolean addNewGroup(int parentId, Changes changes) {
		boolean result = false;
		try {
			DAO.addNewGroup(parentId, changes);
			result = true;
		} catch (ClassNotFoundException | SQLException | NamingException e) {
			this.ErrorLog = "Не удалось добавить группу SKU в БД: \n" + e.getMessage();
			result = false;
		}

		return result;
	}

	// Andrei Duplik added. Returns SKU group with id and name
	public List<DBSKUGroup> getDBSKUgroups() {
		List<DBSKUGroup> DBGroupList = new ArrayList<DBSKUGroup>();

		Map<Integer, String> skuGropMap = new HashMap<>();
		try {
			// groupsList = DAO.getDBSKUgroupsList();

			skuGropMap = DAO.getSkuGroupMap();
			// List<DBSKUGroup> DBGroupList = new ArrayList<DBSKUGroup>();

		} catch (ClassNotFoundException | SQLException | NamingException e) {
			this.ErrorLog = "Не удалось получить список групп SKU из БД: \n" + e.getMessage();
		}

		skuGropMap.entrySet().stream()
				.forEach(t -> DBGroupList.add(new DBSKUGroup(t.getKey().intValue(), t.getValue())));
		return DBGroupList;
	}

	// changed
	public boolean moveSkuToAnotherGroup(SKUGroupChanges c) {
		boolean result = false;

		// In DAO we should send plain objects, not business-layer ones.
		DBSKU sku = new DBSKU();
		sku.setParentId(c.getAfter().getId());
		sku.setId(c.getId());

		try {
			DAO.moveSkuToAnotherGroup(sku);
			result = true;
		} catch (ClassNotFoundException | SQLException | NamingException e) {
			this.ErrorLog = "Не удалось переместить SKU в другую группу: \n" + e.getMessage();
		}
		return result;
	}

	// changed
	public boolean deleteSku(SKUGroupChanges c) {
		boolean result = false;
		DBSKU sku = new DBSKU(c.getId());
		try {
			DAO.deleteSku(sku);
			result = true;
		} catch (ClassNotFoundException | SQLException | NamingException e) {
			this.ErrorLog = "Не удалось удалить SKU: \n" + e.getMessage();
		}

		return result;
	}

	public DBSKUGroup getDBSKUGroupById(int pId) {
		try {
			return DAO.getDBSKUGroupById(pId);
		} catch (ClassNotFoundException | SQLException | NamingException e) {
			this.ErrorLog = "Не удалось запросить в БД группу SKU: \n" + e.getMessage();
		}
		return new DBSKUGroup();
	}

	private String ErrorLog = "";

	public String getErrorLog() {
		return ErrorLog;
	}

	public void setErrorLog(String errorLog) {
		ErrorLog = errorLog;
	}

	private static final long serialVersionUID = 3013221122140047848L;

	@Inject
	private DAO DAO;
}
