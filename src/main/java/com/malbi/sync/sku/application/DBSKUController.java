package com.malbi.sync.sku.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DbRowData;
import com.malbi.sync.sku.service.SKUService;

@ManagedBean(name = "DBSKUController")
@RequestScoped
public class DBSKUController implements Serializable {

	List<Changes> updateList = new ArrayList<>();
	List<Changes> addList = new ArrayList<>();
	List<Changes> removeList = new ArrayList<>();

	public void appendLog(SKUService service, StringBuffer log) {
		String receivedLog = service.getErrorLog();
		// append carrige return if error message is not empty.
		log.append(receivedLog + ((receivedLog.length() == 0) ? "" : "\n"));
	}

	public String goToXLSDownload() {
		return "/xlsdownload.xhtml?faces-redirect=true";
	}

	public String applyChanges() {
		String returnAddress = "";

		SKUService service = new SKUService();
		StringBuffer log = new StringBuffer();

		// move checked SKU
		this.updateList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.moveSkuToAnotherGroup(t);
			if (!result) {
				appendLog(service, log);
			}
		});

		// add checked SKU
		this.addList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.addSkuToDB(t);
			if (!result) {
				appendLog(service, log);
			}
		});

		// remove checked SKU
		this.removeList.stream().filter(t -> t.isChecked()).forEach(t -> {
			boolean result;
			result = service.deleteSku(t);
			if (!result) {
				appendLog(service, log);
			}
		});
		// after all operations.
		if (!log.toString().isEmpty()) {
			this.ExceptionString = log.toString();
		} else {
			returnAddress = "/xlsdownload.xhtml?faces-redirect=true";
		}
		return returnAddress;
	}

	public void appendLogAtRefresh(SKUService service, StringBuffer log) {
		if (!log.toString().isEmpty()) {
			log.append(service.getErrorLog());
		}
	}

	public void refreshData() {
		StringBuffer log = new StringBuffer();
		SKUService service = new SKUService();
		Map<Integer, DbRowData> SKUHierarchy = service.getSkuHierarchyMap();
		appendLogAtRefresh(service, log);

		List<Changes> skuChanges = this.sessionManager.getxSource().getSkuUpdates(SKUHierarchy);

		for (Changes c : skuChanges)

		{
			if (c.getBefore() == null) {
				addList.add(c);
			} else if (c.getAfter() == null) {
				removeList.add(c);
			} else {
				updateList.add(c);
			}
		}

	}

	@PostConstruct
	public void init() {
		refreshData();
	}

	private String ExceptionString;

	public String getExceptionString() {
		return ExceptionString;
	}

	public void setExceptionString(String exceptionString) {
		ExceptionString = exceptionString;
	}

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean sessionManager;

	public LoginBean getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(LoginBean sessionManager) {
		this.sessionManager = sessionManager;
	}

	public List<Changes> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<Changes> updateList) {
		this.updateList = updateList;
	}

	public List<Changes> getAddList() {
		return addList;
	}

	public void setAddList(List<Changes> addList) {
		this.addList = addList;
	}

	public List<Changes> getRemoveList() {
		return removeList;
	}

	public void setRemoveList(List<Changes> removeList) {
		this.removeList = removeList;
	}

	private static final long serialVersionUID = 2396746074529297511L;

}
